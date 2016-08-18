package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;

import com.nfx.android.library.androidgraph.AxisScale.AxisParameters;

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
    public LogXGridLines(AxisParameters axisParameters, float gridLineMinimumValue,
                         float gridLineMaximumValue) {
        super(AxisOrientation.xAxis, axisParameters, gridLineMinimumValue, gridLineMaximumValue);
    }

    @Override
    public void showAxisText(Context context) {
        super.showAxisText(context);
        mAxisText = new XAxisText(context, this, mAxisParameters);
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

    @Override
    float getDimensionLength() {
        return getDrawableArea().getWidth();
    }
}
