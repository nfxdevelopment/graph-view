package com.nfx.android.library.androidgraph;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 */
public abstract class XGridLines extends GridLines {
    public XGridLines(DrawableArea drawableArea, ZoomDisplay zoomDisplay) {
        super(drawableArea, zoomDisplay, AxisOrientation.xAxis);
    }

    public abstract float xIntersect(int gridLine);

    @Override
    public void surfaceChange(DrawableArea drawableArea) {
        super.surfaceChange(drawableArea);
    }

}
