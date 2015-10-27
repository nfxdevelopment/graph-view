package com.nfx.android.library.androidgraph;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 */
public class YGridLines extends GridLines {

    protected int heightInsideGridStoke;

    public YGridLines(DrawableArea drawableArea) {
        super(drawableArea);
    }

    @Override
    public void surfaceChange(DrawableArea drawableArea) {
        super.surfaceChange(drawableArea);

        heightInsideGridStoke = drawableArea.getHeight() - (int) mGridStrokeWidth;
    }
}
