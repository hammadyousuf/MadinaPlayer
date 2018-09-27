package com.kash.kashsoft.lastfm.rest.service;

import android.support.annotation.Nullable;

import com.kash.kashsoft.lastfm.rest.model.LastFmAlbum;
import com.kash.kashsoft.lastfm.rest.model.LastFmArtist;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public interface LastFMService {
    String API_KEY = "c86ff51134c544bf7172aa17897aba0b";
    String BASE_QUERY_PARAMETERS = "?format=json&autocorrect=1&api_key=" + API_KEY;

    @GET(BASE_QUERY_PARAMETERS + "&method=album.getinfo")
    Call<LastFmAlbum> getAlbumInfo(@Query("album") String albumName, @Query("artist") String artistName, @Nullable @Query("lang") String language);

    @GET(BASE_QUERY_PARAMETERS + "&method=artist.getinfo")
    Call<LastFmArtist> getArtistInfo(@Query("artist") String artistName, @Nullable @Query("lang") String language, @Nullable @Header("Cache-Control") String cacheControl);
}