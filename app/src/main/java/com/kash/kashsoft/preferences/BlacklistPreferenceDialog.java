package com.kash.kashsoft.preferences;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Html;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kash.kashsoft.dialogs.BlacklistFolderChooserDialog;
import com.kash.kashsoft.provider.BlacklistStore;

import java.io.File;
import java.util.ArrayList;


public class BlacklistPreferenceDialog extends DialogFragment implements BlacklistFolderChooserDialog.FolderCallback {
    public static final String TAG = BlacklistPreferenceDialog.class.getSimpleName();

    private ArrayList<String> paths;

    public static BlacklistPreferenceDialog newInstance() {
        return new BlacklistPreferenceDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BlacklistFolderChooserDialog blacklistFolderChooserDialog = (BlacklistFolderChooserDialog) getChildFragmentManager().findFragmentByTag("FOLDER_CHOOSER");
        if (blacklistFolderChooserDialog != null) {
            blacklistFolderChooserDialog.setCallback(this);
        }

        refreshBlacklistData();
        return new MaterialDialog.Builder(getContext())
                .title(com.kash.kashsoft.R.string.blacklist)
                .positiveText(android.R.string.ok)
                .neutralText(com.kash.kashsoft.R.string.clear_action)
                .negativeText(com.kash.kashsoft.R.string.add_action)
                .items(paths)
                .autoDismiss(false)
                .itemsCallback((materialDialog, view, i, charSequence) -> new MaterialDialog.Builder(getContext())
                        .title(com.kash.kashsoft.R.string.remove_from_blacklist)
                        .content(Html.fromHtml(getString(com.kash.kashsoft.R.string.do_you_want_to_remove_from_the_blacklist, charSequence)))
                        .positiveText(com.kash.kashsoft.R.string.remove_action)
                        .negativeText(android.R.string.cancel)
                        .onPositive((materialDialog12, dialogAction) -> {
                            BlacklistStore.getInstance(getContext()).removePath(new File(charSequence.toString()));
                            refreshBlacklistData();
                        }).show())
                // clear
                .onNeutral((materialDialog, dialogAction) -> new MaterialDialog.Builder(getContext())
                        .title(com.kash.kashsoft.R.string.clear_blacklist)
                        .content(com.kash.kashsoft.R.string.do_you_want_to_clear_the_blacklist)
                        .positiveText(com.kash.kashsoft.R.string.clear_action)
                        .negativeText(android.R.string.cancel)
                        .onPositive((materialDialog1, dialogAction1) -> {
                            BlacklistStore.getInstance(getContext()).clear();
                            refreshBlacklistData();
                        }).show())
                // add
                .onNegative((materialDialog, dialogAction) -> {
                    BlacklistFolderChooserDialog dialog = BlacklistFolderChooserDialog.create();
                    dialog.setCallback(BlacklistPreferenceDialog.this);
                    dialog.show(getChildFragmentManager(), "FOLDER_CHOOSER");
                })
                .onPositive((materialDialog, dialogAction) -> dismiss())
                .build();
    }

    private void refreshBlacklistData() {
        paths = BlacklistStore.getInstance(getContext()).getPaths();

        MaterialDialog dialog = (MaterialDialog) getDialog();
        if (dialog != null) {
            String[] pathArray = new String[paths.size()];
            pathArray = paths.toArray(pathArray);
            dialog.setItems((CharSequence[]) pathArray);
        }
    }

    @Override
    public void onFolderSelection(@NonNull BlacklistFolderChooserDialog folderChooserDialog, @NonNull File file) {
        BlacklistStore.getInstance(getContext()).addPath(file);
        refreshBlacklistData();
    }
}
