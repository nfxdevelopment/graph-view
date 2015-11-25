package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * NFX Development
 * Created by nick on 29/10/15.
 *
 * Used to draw text onto a canvas to display the values of grid lines in the Y axis
 */
public class YAxisText extends AxisText {
    /**
     * Constructor
     *
     * @param context   application context is used for dimension reasons
     * @param gridLines grid lines axis is related to
     * @param minimumAxisValue the lowest number that the axis displays
     * @param maximumAxisValue the highest number the axis displays
     */
    YAxisText(Context context, GridLines gridLines, float minimumAxisValue,
              float maximumAxisValue) {
        super(context, gridLines, minimumAxisValue, maximumAxisValue);
        mTextPaint.setTextAlign(Paint.Align.RIGHT);
    }

    /**
     * draw the axis text on to canvas
     *
     * @param canvas a canvas to draw onto
     */
    @Override
    public void doDraw(Canvas canvas) {
        for (int i = 0; i < mGridLines.getNumberOfGridLines(); ++i) {
            // The drawing of text happens top to bottom but the scale goes bottom to top. Therefore
            // we need to invert the calculation of the display string
            String displayString = displayString((mGridLines.getNumberOfGridLines() - 1) - i);

            Rect bounds = new Rect();
            mTextPaint.getTextBounds(displayString, 0, displayString.length(), bounds);
            float yIntersect = mGridLines.intersect(i);
            // Ensure the grid line is on screen
            if (yIntersect > (getDrawableArea().getTop() + Math.abs(mTextPaint.ascent())) &&
                    yIntersect < (getDrawableArea().getBottom() -
                            (getRealTextHeight() * 2) - bounds.height())) {
                int y = getDrawableArea().getTop() + (int) yIntersect + (bounds.height() / 2);

                canvas.drawText(displayString, getDrawableArea().getWidth(), y, mTextPaint);
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
    public void calculateRemainingDrawableArea(DrawableArea currentDrawableArea) {
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
