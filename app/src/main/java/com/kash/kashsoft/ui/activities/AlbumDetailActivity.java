package com.kash.kashsoft.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcab.MaterialCab;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.bumptech.glide.Glide;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.kabouzeid.appthemehelper.util.MaterialValueHelper;
import com.kash.kashsoft.adapter.song.AlbumSongAdapter;
import com.kash.kashsoft.dialogs.AddToPlaylistDialog;
import com.kash.kashsoft.dialogs.DeleteSongsDialog;
import com.kash.kashsoft.dialogs.SleepTimerDialog;
import com.kash.kashsoft.glide.mzColoredTarget;
import com.kash.kashsoft.glide.SongGlideRequest;
import com.kash.kashsoft.helper.MusicPlayerRemote;
import com.kash.kashsoft.interfaces.CabHolder;
import com.kash.kashsoft.interfaces.LoaderIds;
import com.kash.kashsoft.interfaces.PaletteColorHolder;
import com.kash.kashsoft.lastfm.rest.LastFMRestClient;
import com.kash.kashsoft.lastfm.rest.model.LastFmAlbum;
import com.kash.kashsoft.loader.AlbumLoader;
import com.kash.kashsoft.misc.SimpleObservableScrollViewCallbacks;
import com.kash.kashsoft.misc.WrappedAsyncTaskLoader;
import com.kash.kashsoft.model.Album;
import com.kash.kashsoft.model.Song;
import com.kash.kashsoft.ui.activities.base.AbsSlidingMusicPanelActivity;
import com.kash.kashsoft.ui.activities.tageditor.AbsTagEditorActivity;
import com.kash.kashsoft.ui.activities.tageditor.AlbumTagEditorActivity;
import com.kash.kashsoft.util.MusicUtil;
import com.kash.kashsoft.util.NavigationUtil;
import com.kash.kashsoft.util.mzplayerColorUtil;
import com.kash.kashsoft.util.Util;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Be careful when changing things in this Activity!
 */
public class AlbumDetailActivity extends AbsSlidingMusicPanelActivity implements PaletteColorHolder, CabHolder, LoaderManager.LoaderCallbacks<Album> {

    public static final String TAG = AlbumDetailActivity.class.getSimpleName();
    private static final int TAG_EDITOR_REQUEST = 2001;
    private static final int LOADER_ID = LoaderIds.ALBUM_DETAIL_ACTIVITY;

    public static final String EXTRA_ALBUM_ID = "extra_album_id";

    private Album album;

    @BindView(com.kash.kashsoft.R.id.list)
    ObservableRecyclerView recyclerView;
    @BindView(com.kash.kashsoft.R.id.image)
    ImageView albumArtImageView;
    @BindView(com.kash.kashsoft.R.id.toolbar)
    Toolbar toolbar;
    @BindView(com.kash.kashsoft.R.id.header)
    View headerView;
    @BindView(com.kash.kashsoft.R.id.header_overlay)
    View headerOverlay;

    @BindView(com.kash.kashsoft.R.id.artist_icon)
    ImageView artistIconImageView;
    @BindView(com.kash.kashsoft.R.id.duration_icon)
    ImageView durationIconImageView;
    @BindView(com.kash.kashsoft.R.id.song_count_icon)
    ImageView songCountIconImageView;
    @BindView(com.kash.kashsoft.R.id.album_year_icon)
    ImageView albumYearIconImageView;
    @BindView(com.kash.kashsoft.R.id.artist_text)
    TextView artistTextView;
    @BindView(com.kash.kashsoft.R.id.duration_text)
    TextView durationTextView;
    @BindView(com.kash.kashsoft.R.id.song_count_text)
    TextView songCountTextView;
    @BindView(com.kash.kashsoft.R.id.album_year_text)
    TextView albumYearTextView;

    private AlbumSongAdapter adapter;

    private MaterialCab cab;
    private int headerViewHeight;
    private int toolbarColor;

    @Nullable
    private Spanned wiki;
    private MaterialDialog wikiDialog;
    private LastFMRestClient lastFMRestClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDrawUnderStatusbar(true);
        ButterKnife.bind(this);

        lastFMRestClient = new LastFMRestClient(this);

        setUpObservableListViewParams();
        setUpToolBar();
        setUpViews();

        getSupportLoaderManager().initLoader(LOADER_ID, getIntent().getExtras(), this);
    }

    @Override
    protected View createContentView() {
        return wrapSlidingMusicPanel(com.kash.kashsoft.R.layout.activity_album_detail);
    }

    private final SimpleObservableScrollViewCallbacks observableScrollViewCallbacks = new SimpleObservableScrollViewCallbacks() {
        @Override
        public void onScrollChanged(int scrollY, boolean b, boolean b2) {
            scrollY += headerViewHeight;

            // Change alpha of overlay
            float headerAlpha = Math.max(0, Math.min(1, (float) 2 * scrollY / headerViewHeight));
            headerOverlay.setBackgroundColor(ColorUtil.withAlpha(toolbarColor, headerAlpha));

            // Translate name text
            headerView.setTranslationY(Math.max(-scrollY, -headerViewHeight));
            headerOverlay.setTranslationY(Math.max(-scrollY, -headerViewHeight));
            albumArtImageView.setTranslationY(Math.max(-scrollY, -headerViewHeight));
        }
    };

    private void setUpObservableListViewParams() {
        headerViewHeight = getResources().getDimensionPixelSize(com.kash.kashsoft.R.dimen.detail_header_height);
    }

    private void setUpViews() {
        setUpRecyclerView();
        setUpSongsAdapter();
        artistTextView.setOnClickListener(v -> {
            if (album != null) {
                NavigationUtil.goToArtist(AlbumDetailActivity.this, album.getArtistId());
            }
        });
        setColors(DialogUtils.resolveColor(this, com.kash.kashsoft.R.attr.defaultFooterColor));
    }

    private void loadAlbumCover() {
        SongGlideRequest.Builder.from(Glide.with(this), getAlbum().safeGetFirstSong())
                .checkIgnoreMediaStore(this)
                .generatePalette(this).build()
                .dontAnimate()
                .into(new mzColoredTarget(albumArtImageView) {
                    @Override
                    public void onColorReady(int color) {
                        setColors(color);
                    }
                });
    }

    private void setColors(int color) {
        toolbarColor = color;
        headerView.setBackgroundColor(color);

        setNavigationbarColor(color);
        setTaskDescriptionColor(color);

        toolbar.setBackgroundColor(color);
        setSupportActionBar(toolbar); // needed to auto readjust the toolbar content color
        setStatusbarColor(color);

        int secondaryTextColor = MaterialValueHelper.getSecondaryTextColor(this, ColorUtil.isColorLight(color));
        artistIconImageView.setColorFilter(secondaryTextColor, PorterDuff.Mode.SRC_IN);
        durationIconImageView.setColorFilter(secondaryTextColor, PorterDuff.Mode.SRC_IN);
        songCountIconImageView.setColorFilter(secondaryTextColor, PorterDuff.Mode.SRC_IN);
        albumYearIconImageView.setColorFilter(secondaryTextColor, PorterDuff.Mode.SRC_IN);
        artistTextView.setTextColor(MaterialValueHelper.getPrimaryTextColor(this, ColorUtil.isColorLight(color)));
        durationTextView.setTextColor(secondaryTextColor);
        songCountTextView.setTextColor(secondaryTextColor);
        albumYearTextView.setTextColor(secondaryTextColor);
    }

    @Override
    public int getPaletteColor() {
        return toolbarColor;
    }

    private void setUpRecyclerView() {
        setUpRecyclerViewPadding();
        recyclerView.setScrollViewCallbacks(observableScrollViewCallbacks);
        final View contentView = getWindow().getDecorView().findViewById(android.R.id.content);
        contentView.post(() -> observableScrollViewCallbacks.onScrollChanged(-headerViewHeight, false, false));
    }

    private void setUpRecyclerViewPadding() {
        recyclerView.setPadding(0, headerViewHeight, 0, 0);
    }

    private void setUpToolBar() {
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpSongsAdapter() {
        adapter = new AlbumSongAdapter(this, getAlbum().songs, com.kash.kashsoft.R.layout.item_list, false, this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (adapter.getItemCount() == 0) finish();
            }
        });
    }

    private void reload() {
        getSupportLoaderManager().restartLoader(LOADER_ID, getIntent().getExtras(), this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.kash.kashsoft.R.menu.menu_album_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void loadWiki() {
        loadWiki(Locale.getDefault().getLanguage());
    }

    private void loadWiki(@Nullable final String lang) {
        wiki = null;

        lastFMRestClient.getApiService()
                .getAlbumInfo(getAlbum().getTitle(), getAlbum().getArtistName(), lang)
                .enqueue(new Callback<LastFmAlbum>() {
                    @Override
                    public void onResponse(@NonNull Call<LastFmAlbum> call, @NonNull Response<LastFmAlbum> response) {
                        final LastFmAlbum lastFmAlbum = response.body();
                        if (lastFmAlbum != null && lastFmAlbum.getAlbum() != null && lastFmAlbum.getAlbum().getWiki() != null) {
                            final String wikiContent = lastFmAlbum.getAlbum().getWiki().getContent();
                            if (wikiContent != null && !wikiContent.trim().isEmpty()) {
                                wiki = Html.fromHtml(wikiContent);
                            }
                        }

                        // If the "lang" parameter is set and no wiki is given, retry with default language
                        if (wiki == null && lang != null) {
                            loadWiki(null);
                            return;
                        }

                        if (!Util.isAllowedToDownloadMetadata(AlbumDetailActivity.this)) {
                            if (wiki != null) {
                                wikiDialog.setContent(wiki);
                            } else {
                                wikiDialog.dismiss();
                                Toast.makeText(AlbumDetailActivity.this, getResources().getString(com.kash.kashsoft.R.string.wiki_unavailable), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<LastFmAlbum> call, @NonNull Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        final ArrayList<Song> songs = adapter.getDataSet();
        switch (id) {
            case com.kash.kashsoft.R.id.action_sleep_timer:
                new SleepTimerDialog().show(getSupportFragmentManager(), "SET_SLEEP_TIMER");
                return true;
            case com.kash.kashsoft.R.id.action_equalizer:
                NavigationUtil.openEqualizer(this);
                return true;
            case com.kash.kashsoft.R.id.action_shuffle_album:
                MusicPlayerRemote.openAndShuffleQueue(songs, true);
                return true;
            case com.kash.kashsoft.R.id.action_play_next:
                MusicPlayerRemote.playNext(songs);
                return true;
            case com.kash.kashsoft.R.id.action_add_to_current_playing:
                MusicPlayerRemote.enqueue(songs);
                return true;
            case com.kash.kashsoft.R.id.action_add_to_playlist:
                AddToPlaylistDialog.create(songs).show(getSupportFragmentManager(), "ADD_PLAYLIST");
                return true;
            case com.kash.kashsoft.R.id.action_delete_from_device:
                DeleteSongsDialog.create(songs).show(getSupportFragmentManager(), "DELETE_SONGS");
                return true;
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case com.kash.kashsoft.R.id.action_tag_editor:
                Intent intent = new Intent(this, AlbumTagEditorActivity.class);
                intent.putExtra(AbsTagEditorActivity.EXTRA_ID, getAlbum().getId());
                startActivityForResult(intent, TAG_EDITOR_REQUEST);
                return true;
            case com.kash.kashsoft.R.id.action_go_to_artist:
                NavigationUtil.goToArtist(this, getAlbum().getArtistId());
                return true;
            case com.kash.kashsoft.R.id.action_wiki:
                if (wikiDialog == null) {
                    wikiDialog = new MaterialDialog.Builder(this)
                            .title(album.getTitle())
                            .positiveText(android.R.string.ok)
                            .build();
                }
                if (Util.isAllowedToDownloadMetadata(this)) {
                    if (wiki != null) {
                        wikiDialog.setContent(wiki);
                        wikiDialog.show();
                    } else {
                        Toast.makeText(this, getResources().getString(com.kash.kashsoft.R.string.wiki_unavailable), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    wikiDialog.show();
                    loadWiki();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAG_EDITOR_REQUEST) {
            reload();
            setResult(RESULT_OK);
        }
    }

    @NonNull
    @Override
    public MaterialCab openCab(int menuRes, @NonNull final MaterialCab.Callback callback) {
        if (cab != null && cab.isActive()) cab.finish();
        cab = new MaterialCab(this, com.kash.kashsoft.R.id.cab_stub)
                .setMenu(menuRes)
                .setCloseDrawableRes(com.kash.kashsoft.R.drawable.ic_close_white_24dp)
                .setBackgroundColor(mzplayerColorUtil.shiftBackgroundColorForLightText(getPaletteColor()))
                .start(new MaterialCab.Callback() {
                    @Override
                    public boolean onCabCreated(MaterialCab materialCab, Menu menu) {
                        return callback.onCabCreated(materialCab, menu);
                    }

                    @Override
                    public boolean onCabItemClicked(MenuItem menuItem) {
                        return callback.onCabItemClicked(menuItem);
                    }

                    @Override
                    public boolean onCabFinished(MaterialCab materialCab) {
                        return callback.onCabFinished(materialCab);
                    }
                });
        return cab;
    }

    @Override
    public void onBackPressed() {
        if (cab != null && cab.isActive()) cab.finish();
        else {
            recyclerView.stopScroll();
            super.onBackPressed();
        }
    }

    @Override
    public void onMediaStoreChanged() {
        super.onMediaStoreChanged();
        reload();
    }

    @Override
    public void setStatusbarColor(int color) {
        super.setStatusbarColor(color);
        setLightStatusbar(false);
    }

    private void setAlbum(Album album) {
        this.album = album;
        loadAlbumCover();

        if (Util.isAllowedToDownloadMetadata(this)) {
            loadWiki();
        }

        getSupportActionBar().setTitle(album.getTitle());
        artistTextView.setText(album.getArtistName());
        songCountTextView.setText(MusicUtil.getSongCountString(this, album.getSongCount()));
        durationTextView.setText(MusicUtil.getReadableDurationString(MusicUtil.getTotalDuration(this, album.songs)));
        albumYearTextView.setText(album.getYear() > 0 ? String.valueOf(album.getYear()) : "-");

        adapter.swapDataSet(album.songs);
    }

    private Album getAlbum() {
        if (album == null) album = new Album();
        return album;
    }

    @Override
    public Loader<Album> onCreateLoader(int id, Bundle args) {
        return new AsyncAlbumLoader(this, args.getInt(EXTRA_ALBUM_ID));
    }

    @Override
    public void onLoadFinished(Loader<Album> loader, Album data) {
        setAlbum(data);
    }

    @Override
    public void onLoaderReset(Loader<Album> loader) {
        this.album = new Album();
        adapter.swapDataSet(album.songs);
    }

    private static class AsyncAlbumLoader extends WrappedAsyncTaskLoader<Album> {
        private final int albumId;

        public AsyncAlbumLoader(Context context, int albumId) {
            super(context);
            this.albumId = albumId;
        }

        @Override
        public Album loadInBackground() {
            return AlbumLoader.getAlbum(getContext(), albumId);
        }
    }
}