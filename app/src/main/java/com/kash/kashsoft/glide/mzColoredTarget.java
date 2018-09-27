package com.kash.kashsoft.glide;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.kabouzeid.appthemehelper.util.ATHUtil;
import com.kash.kashsoft.glide.palette.BitmapPaletteTarget;
import com.kash.kashsoft.glide.palette.BitmapPaletteWrapper;
import com.kash.kashsoft.util.mzplayerColorUtil;

public abstract class mzColoredTarget extends BitmapPaletteTarget {
    public mzColoredTarget(ImageView view) {
        super(view);
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        super.onLoadFailed(e, errorDrawable);
        onColorReady(getDefaultFooterColor());
    }

    @Override
    public void onResourceReady(BitmapPaletteWrapper resource, GlideAnimation<? super BitmapPaletteWrapper> glideAnimation) {
        super.onResourceReady(resource, glideAnimation);
        onColorReady(mzplayerColorUtil.getColor(resource.getPalette(), getDefaultFooterColor()));
    }

    protected int getDefaultFooterColor() {
        return ATHUtil.resolveColor(getView().getContext(), com.kash.kashsoft.R.attr.defaultFooterColor);
    }

    protected int getAlbumArtistFooterColor() {
        return ATHUtil.resolveColor(getView().getContext(), com.kash.kashsoft.R.attr.cardBackgroundColor);
    }

    public abstract void onColorReady(int color);
}
