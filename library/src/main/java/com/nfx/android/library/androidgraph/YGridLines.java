package com.nfx.android.library.androidgraph;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 */
public abstract class YGridLines extends GridLines {

    protected int heightOfViewInsideGridStoke;

    public YGridLines(DrawableArea drawableArea) {
        super(drawableArea, AxisOrientation.yAxis);
    }

    public abstract float yIntersect(int gridLine);

    @Override
    public void surfaceChange(DrawableArea drawableArea) {
        super.surfaceChange(drawableArea);

        heightOfViewInsideGridStoke = drawableArea.getHeight() - (int) mGridStrokeWidth;
    }
}
