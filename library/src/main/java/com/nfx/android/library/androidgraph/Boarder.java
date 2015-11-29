package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * NFX Development
 * Created by nick on 28/10/15.
 *
 * This will draw 4 lines around the outer edge of the viewable area.
 * It will also take into account the stroke width to ensure the whole line is viewable
 */
public class Boarder extends DrawableObject {
    /**
     * Stroke width of the boarder
     */
    private static final int STROKE_WIDTH = 10;
    /**
     * Color of the boarder
     */
    private static final int COLOR = Color.GRAY;
    /**
     * Paint to Draw with
     */
    private final Paint mPaint = new Paint();

    public Boarder() {
        mPaint.setColor(COLOR);
        mPaint.setStrokeWidth(STROKE_WIDTH);
    }

    /**
     * Called from BackManager when a draw is needed
     *
     * @param canvas draw directly onto this canvas
     */
    @Override
    public void doDraw(Canvas canvas) {
        int halfStrokeWidth = STROKE_WIDTH / 2;

        // Draw the left boarder
        canvas.drawLine(getDrawableArea().getLeft() + halfStrokeWidth, getDrawableArea().getTop(),
                getDrawableArea().getLeft() + halfStrokeWidth, getDrawableArea().getBottom(),
                mPaint);
        // Draw the right boarder
        canvas.drawLine(getDrawableArea().getRight() - halfStrokeWidth, getDrawableArea().getTop(),
                getDrawableArea().getRight() - halfStrokeWidth, getDrawableArea().getBottom(),
                mPaint);
        // Draw the top boarder
        canvas.drawLine(getDrawableArea().getLeft(), getDrawableArea().getTop() + halfStrokeWidth,
                getDrawableArea().getRight(), getDrawableArea().getTop() + halfStrokeWidth, mPaint);
        // draw the bottom boarder
        canvas.drawLine(getDrawableArea().getLeft(),
                getDrawableArea().getBottom() - halfStrokeWidth,
                getDrawableArea().getRight(),
                getDrawableArea().getBottom() - halfStrokeWidth, mPaint);
    }

    /**
     * returns the stroke width of the boarder
     *
     * @return stroke width only
     */
    public int getStrokeWidth() {
        return STROKE_WIDTH;
    }

    /**
     * This will change the drawable area passed in to reflect the new drawable area after the
     * Boarder object is finished with it
     *
     * @param currentDrawableArea will reflect the new drawable area pass in current drawableArea
     */
    @Override
    public void calculateRemainingDrawableArea(DrawableArea currentDrawableArea) {
        int xOffset = currentDrawableArea.getLeft() + STROKE_WIDTH;
        int yOffset = currentDrawableArea.getTop() + STROKE_WIDTH;
        int width = currentDrawableArea.getWidth() - (STROKE_WIDTH * 2);
        int height = currentDrawableArea.getHeight() - (STROKE_WIDTH * 2);

        currentDrawableArea.setDrawableArea(xOffset, yOffset, width, height);
    }
}
