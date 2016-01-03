package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 *
 * Draws logarithmic style grid lines in the x plane. Call doDraw with the canvas in which to
 * draw on
 */
public class LogXGridLines extends LogGridLines {

    /**
     * Constructor
     */
    public LogXGridLines(float axisValueSpan, float logOffset, float decade) {
        super(AxisOrientation.xAxis, axisValueSpan, logOffset, decade);
    }

    @Override
    public void showAxisText(Context context, float minimumValue, float maximumValue) {
        super.showAxisText(context, minimumValue, maximumValue);
        mAxisText = new LogXAxisText(context, this, minimumValue, maximumValue);
    }

    /**
     * Draws all lines which are viewable on screen
     *
     * @param canvas a canvas to draw onto
     */
    @Override
    public void doDraw(Canvas canvas) {
        super.doDraw(canvas);
        float xIntersect;
        for (int i = 0; i < mNumberOfGridLines; ++i) {
            xIntersect = intersectZoomCompensated(i) * getDrawableArea().getWidth();
            if(xIntersect >= 0 && xIntersect < getDimensionLength()) {
                canvas.drawLine(getDrawableArea().getLeft() + xIntersect,
                        getDrawableArea().getTop(),
                        getDrawableArea().getLeft() + xIntersect, getDrawableArea().getBottom(),
                        mPaint);

            }
        }
    }

    /**
     * The surface size has changed update the current object to resize drawing
     *
     * @param drawableArea new surface size
     */
    public void surfaceChanged(DrawableArea drawableArea) {
        super.surfaceChanged(drawableArea);
        setGridLinesOffset(0);

    }

    @Override
    float getDimensionLength() {
        return getDrawableArea().getWidth();
    }
}
