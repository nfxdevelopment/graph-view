package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.nfx.android.library.androidgraph.AxisScale.AxisParameters;

/**
 * NFX Development
 * Created by nick on 29/10/15.
 * <p/>
 * Used to draw text onto a canvas to display the values of grid lines in the Y axis
 */
class YAxisText extends AxisText {
    /**
     * Constructor
     *
     * @param context           application context is used for dimension reasons
     * @param gridLines         grid lines axis is related to
     * @param axisParameters    graph limits
     */
    YAxisText(Context context, GridLines gridLines, AxisParameters axisParameters) {
        super(context, gridLines, axisParameters);
        mPaint.setTextAlign(Paint.Align.RIGHT);
    }

    /**
     * draw the axis text on to canvas
     *
     * @param canvas a canvas to draw onto
     */
    @Override
    public void doDraw(Canvas canvas) {

        float lastTextDrawn = mGridLines.getDrawableArea().getHeight() -
                mGridLines.intersectZoomCompensated(0) *
                        mGridLines.getDrawableArea().getHeight();
        if(lastTextDrawn > mGridLines.getDrawableArea().getHeight()) {
            lastTextDrawn = mGridLines.getDrawableArea().getHeight();
        }
        final int lastGridLine = mGridLines.getNumberOfGridLines() - 1;
        float drawLimitText = mGridLines.getDrawableArea().getHeight() -
                mGridLines.intersectZoomCompensated(lastGridLine) *
                        mGridLines.getDrawableArea().getHeight();
        if(drawLimitText > mGridLines.getDrawableArea().getHeight()) {
            drawLimitText = mGridLines.getDrawableArea().getHeight();
        } else if(drawLimitText < 0) {
            drawLimitText = 0;
        }

        // Our limits are over laps with other grid lines, hence starting from 1 and -1
        for(int i = 1; i < lastGridLine; ++i) {
            String displayString = mGridLineValues[i];

            float yIntersect = mGridLines.getDrawableArea().getHeight() -
                    mGridLines.intersectZoomCompensated(i) *
                            mGridLines.getDrawableArea().getHeight();
            // Ensure the grid line is on screen
            if(yIntersect < mGridLines.getDrawableArea().getHeight() &&
                    lastTextDrawn - yIntersect > getRealTextHeight() * 1f &&
                    yIntersect - drawLimitText > getRealTextHeight() * 1f) {
                float y = getDrawableArea().getTop() + yIntersect + (getRealTextHeight() / 2);

                canvas.drawText(displayString, getDrawableArea().getWidth(), y, mPaint);

                lastTextDrawn = yIntersect;
            }
        }
    }

    /**
     * The surface size has changed update the current object to resize drawing
     * This will align the yAxis to the top left for now
     *
     * @param drawableArea new surface size
     */
    @Override
    public void surfaceChanged(DrawableArea drawableArea) {
        getDrawableArea().setDrawableArea(drawableArea.getLeft(), drawableArea.getTop(),
                (int) getMaximumTextWidth(), drawableArea.getHeight());
        calculateRemainingDrawableArea(drawableArea);
    }

    /**
     * This will change the drawable area passed in to reflect the new drawable area after the
     * Axis object is finished with it
     *
     * @param currentDrawableArea will reflect the new drawable area pass in current drawableArea
     */
    @Override
    protected void calculateRemainingDrawableArea(DrawableArea currentDrawableArea) {
        int xOffset = currentDrawableArea.getLeft();
        int yOffset = currentDrawableArea.getTop();
        int width = currentDrawableArea.getWidth();
        int height = currentDrawableArea.getHeight();

        // If it is equal to zero we assume it is top aligned
        if (getDrawableArea().getLeft() == 0) {
            xOffset += getDrawableArea().getWidth();
        }

        width -= getDrawableArea().getWidth();

        currentDrawableArea.setDrawableArea(xOffset, yOffset, width, height);
    }
}
