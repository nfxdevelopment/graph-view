package com.nfx.android.graph.androidgraph;

import android.content.Context;
import android.graphics.Canvas;

import com.nfx.android.graph.androidgraph.AxisScale.AxisParameters;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 *
 * Draws linear style grid lines in the y plane. Call doDraw with the canvas in which to draw on
 */
class LinYGridLines extends LinGridLines {
    /**
     * Constructor
     *
     * @param axisParameters        parameters of graph shown
     */
    LinYGridLines(AxisParameters axisParameters) {
        super(AxisOrientation.yAxis, axisParameters);
    }

    /**
     * Draws all lines which are viewable on screen
     *
     * @param canvas a canvas to draw onto
     */
    @Override
    public void doDraw(Canvas canvas) {
        super.doDraw(canvas);

        for (int i = 0; i < mNumberOfGridLines; ++i) {
            float yIntersect = getDrawableArea().getHeight() - (intersectZoomCompensated(i) *
                    getDrawableArea().getHeight());
            if(yIntersect >= 0 && yIntersect < getDimensionLength()) {
                canvas.drawLine(getDrawableArea().getLeft(), getDrawableArea().getTop() +
                        yIntersect,
                        getDrawableArea().getRight(), getDrawableArea().getTop() + yIntersect,
                        mPaint);

            }
        }
    }

    @Override
    public void showAxisText(Context context) {
        super.showAxisText(context);
        mAxisText = new YAxisText(context, this, mAxisParameters);
    }

    @Override
    float getDimensionLength() {
        return getDrawableArea().getHeight();
    }
}
