package com.nfx.android.graph.androidgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.nfx.android.graph.androidgraph.AxisScale.AxisParameters;

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
        paint.setTextAlign(Paint.Align.RIGHT);
    }

    /**
     * draw the axis text on to canvas
     *
     * @param canvas a canvas to draw onto
     */
    @Override
    public void doDraw(Canvas canvas) {
        if(gridLineValues.length != gridLines.getNumberOfGridLines()) {
            gridLineValues = new String[gridLines.getNumberOfGridLines()];
            calculateGridLineValues();
        }

        float lastTextDrawn = gridLines.getDrawableArea().getHeight() -
                gridLines.intersectZoomCompensated(0) *
                        gridLines.getDrawableArea().getHeight();
        if(lastTextDrawn > gridLines.getDrawableArea().getHeight()) {
            lastTextDrawn = gridLines.getDrawableArea().getHeight();
        }
        final int lastGridLine = gridLines.getNumberOfGridLines() - 1;
        float drawLimitText = gridLines.getDrawableArea().getHeight() -
                gridLines.intersectZoomCompensated(lastGridLine) *
                        gridLines.getDrawableArea().getHeight();
        if(drawLimitText > gridLines.getDrawableArea().getHeight()) {
            drawLimitText = gridLines.getDrawableArea().getHeight();
        } else if(drawLimitText < 0) {
            drawLimitText = 0;
        }

        // Our limits are over laps with other grid lines, hence starting from 1 and -1
        for(int i = 1; i < lastGridLine; ++i) {
            String displayString = gridLineValues[i];

            float yIntersect = gridLines.getDrawableArea().getHeight() -
                    gridLines.intersectZoomCompensated(i) *
                            gridLines.getDrawableArea().getHeight();
            // Ensure the grid line is on screen
            if(yIntersect < gridLines.getDrawableArea().getHeight() &&
                    lastTextDrawn - yIntersect > getRealTextHeight() * 1f &&
                    yIntersect - drawLimitText > getRealTextHeight() * 1f) {
                float y = getDrawableArea().getTop() + yIntersect + (getRealTextHeight() / 2);

                canvas.drawText(displayString, getDrawableArea().getWidth(), y, paint);

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
