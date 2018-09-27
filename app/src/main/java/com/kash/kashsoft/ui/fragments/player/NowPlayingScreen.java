package com.kash.kashsoft.ui.fragments.player;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public enum NowPlayingScreen {
    CARD(com.kash.kashsoft.R.string.card, com.kash.kashsoft.R.drawable.np_card, 0),
    FLAT(com.kash.kashsoft.R.string.flat, com.kash.kashsoft.R.drawable.np_flat, 1);

    @StringRes
    public final int titleRes;
    @DrawableRes
    public final int drawableResId;
    public final int id;

    NowPlayingScreen(@StringRes int titleRes, @DrawableRes int drawableResId, int id) {
        this.titleRes = titleRes;
        this.drawableResId = drawableResId;
        this.id = id;
    }
}
