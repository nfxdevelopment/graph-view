package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * NFX Development
 * Created by nick on 29/10/15.
 *
 * Used to draw text onto a canvas to display the values of grid lines in the Y axis
 */
public class XAxisText extends AxisText {
    /**
     * Constructor
     *
     * @param context   application context is used for dimension reasons
     * @param gridLines grid lines axis is related to
     * @param minimumAxisValue the lowest number that the axis displays
     * @param maximumAxisValue the highest number the axis displays
     */
    XAxisText(Context context, GridLines gridLines, float minimumAxisValue,
              float maximumAxisValue) {
        super(context, gridLines, minimumAxisValue, maximumAxisValue);
    }

    /**
     * draw the axis text on to canvas. Note this will only draw text if there is more than 1
     * grid line
     *
     * @param canvas a canvas to draw onto
     */
    @Override
    public void doDraw(Canvas canvas) {
        float lastTextDrawn = mGridLines.intersectZoomCompensated(0) *
                mGridLines.getDrawableArea().getWidth();
        if(lastTextDrawn < 0) {
            lastTextDrawn = 0;
        }
        final int lastGridLine = mGridLines.getNumberOfGridLines() - 1;
        float drawLimitText = mGridLines.intersectZoomCompensated(lastGridLine) *
                mGridLines.getDrawableArea().getWidth();
        if(drawLimitText < 0) {
            drawLimitText = getDrawableArea().getWidth();
        }

        // Our limits are over laps with other grid lines, hence starting from 1 and limit -1
        for(int i = 1; i < lastGridLine; ++i) {

            float xIntersect = mGridLines.intersectZoomCompensated(i) *
                    mGridLines.getDrawableArea().getWidth();

            if(xIntersect > 0 &&
                    xIntersect - lastTextDrawn > mBounds.width() &&
                    drawLimitText - xIntersect > mBounds.width() * 1.5f) {
                String displayString = mGridLineValues[i];
                int x = getDrawableArea().getLeft() + (int) xIntersect;

                // Remember the text is drawn on the baseline
                canvas.drawText(displayString, x, getDrawableArea()
                        .getTop() +
                        (int) Math.abs(mTextPaint.ascent()), mTextPaint);

                lastTextDrawn = xIntersect;
            }
        }
    }

    /**
     * The surface size has changed update the current object to resize drawing
     * This will align the xAxis to the bottom for now
     *
     * @param drawableArea new surface size
     */
    public void surfaceChanged(DrawableArea drawableArea) {
        String textString = "0";
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(textString, 0, textString.length(), bounds);
        getDrawableArea().setDrawableArea(drawableArea.getLeft(),
                drawableArea.getHeight() - (int) getRealTextHeight(),
                drawableArea.getWidth(), (int) getRealTextHeight());
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
        if (getDrawableArea().getTop() == 0) {
            yOffset += getDrawableArea().getHeight();
        }

        height -= getDrawableArea().getHeight();

        currentDrawableArea.setDrawableArea(xOffset, yOffset, width, height);
    }
}
