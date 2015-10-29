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
public class YAxisText extends AxisText {
    /**
     * Constructor
     *
     * @param context   application context is used for dimension reasons
     * @param gridLines grid lines axis is related to
     */
    YAxisText(Context context, GridLines gridLines) {
        super(context, gridLines);
    }

    /**
     * draw the axis text on to canvas
     *
     * @param canvas a canvas to draw onto
     */
    @Override
    public void doDraw(Canvas canvas) {
        for (int i = 0; i < mGridLines.getNumberOfGridLines(); ++i) {
            Rect bounds = new Rect();
            String textString = String.valueOf(i);
            mTextPaint.getTextBounds(textString, 0, textString.length(), bounds);
            int y = getDrawableArea().getTop() + (int) mGridLines.intersect(i)
                    + (bounds.height() / 2);

            canvas.drawText(textString, getDrawableArea().getWidth() / 2, y, mTextPaint);
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
