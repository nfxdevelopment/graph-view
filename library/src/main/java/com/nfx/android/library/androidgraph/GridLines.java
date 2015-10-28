package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Color;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 */
public class GridLines extends DrawableObject {

    protected int mNumberOfGridLines = 4;
    protected int mGridColor = Color.GRAY;
    /**
     * Use an even number to ensure all grid line strokes look the same
     */
    protected float mGridStrokeWidth = 4f;
    /**
     * Describes the viewable part of the grid
     */
    ZoomDisplay mZoomDisplay;
    /**
     * This allows us to know the axis at runtime
     */
    private AxisOrientation mAxisOrientation;

    public GridLines(DrawableArea drawableArea, ZoomDisplay zoomDisplay, AxisOrientation
            axisOrientation) {
        super(drawableArea);
        mZoomDisplay = zoomDisplay;
        mAxisOrientation = axisOrientation;
    }

    public AxisOrientation getAxisOrientation() {
        return mAxisOrientation;
    }

    public int getNumberOfGridLines() {
        return mNumberOfGridLines;
    }

    @Override
    public void doDraw(Canvas canvas) {

    }

    public void setGridStrokeWidth(int strokeWidth) {
        mGridStrokeWidth = strokeWidth;
    }

    public void setColor(int color) {
        mGridColor = color;
    }

    enum AxisOrientation {
        xAxis,
        yAxis
    }
}
