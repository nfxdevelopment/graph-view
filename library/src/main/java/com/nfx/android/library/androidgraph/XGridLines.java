package com.nfx.android.library.androidgraph;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 */
public class XGridLines extends GridLines {

    protected int widthInsideGridStoke;

    public XGridLines(DrawableArea drawableArea) {
        super(drawableArea);
    }

    @Override
    public void surfaceChange(DrawableArea drawableArea) {
        super.surfaceChange(drawableArea);

        widthInsideGridStoke = drawableArea.getWidth() - (int) mGridStrokeWidth;
    }

}
