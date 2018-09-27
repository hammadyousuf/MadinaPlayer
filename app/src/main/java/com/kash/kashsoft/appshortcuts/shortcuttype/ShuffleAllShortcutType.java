package com.kash.kashsoft.appshortcuts.shortcuttype;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.os.Build;

import com.kash.kashsoft.appshortcuts.AppShortcutIconGenerator;
import com.kash.kashsoft.appshortcuts.AppShortcutLauncherActivity;


@TargetApi(Build.VERSION_CODES.N_MR1)
public final class ShuffleAllShortcutType extends BaseShortcutType {
    public ShuffleAllShortcutType(Context context) {
        super(context);
    }

    public static String getId() {
        return ID_PREFIX + "shuffle_all";
    }

    public ShortcutInfo getShortcutInfo() {
        return new ShortcutInfo.Builder(context, getId())
                .setShortLabel(context.getString(com.kash.kashsoft.R.string.app_shortcut_shuffle_all_short))
                .setLongLabel(context.getString(com.kash.kashsoft.R.string.action_shuffle_all))
                .setIcon(AppShortcutIconGenerator.generateThemedIcon(context, com.kash.kashsoft.R.drawable.ic_app_shortcut_shuffle_all))
                .setIntent(getPlaySongsIntent(AppShortcutLauncherActivity.SHORTCUT_TYPE_SHUFFLE_ALL))
                .build();
    }
}
