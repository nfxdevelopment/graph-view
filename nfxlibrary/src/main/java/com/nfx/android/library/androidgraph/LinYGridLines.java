package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 *
 * Draws linear style grid lines in the y plane. Call doDraw with the canvas in which to draw on
 */
public class LinYGridLines extends LinGridLines {
    /**
     * Constructor
     */
    public LinYGridLines() {
        super(AxisOrientation.yAxis);
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
    public void showAxisText(Context context, float minimumValue, float maximumValue) {
        super.showAxisText(context, minimumValue, maximumValue);
        mAxisText = new YAxisText(context, this, minimumValue, maximumValue);
    }

    @Override
    float getDimensionLength() {
        return getDrawableArea().getHeight();
    }
}
