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
     * Stroke width of the board line
     */
    private static final int mStrokeWidth = 4;
    /**
     * Color of the boarder this can be updated at runtime
     */
    private int color = Color.GRAY;

    /**
     * Called from BackManager when a draw is needed
     * @param canvas draw directly onto this canvas
     */
    @Override
    public void doDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(mStrokeWidth);

        int halfStrokeWidth = mStrokeWidth / 2;

        // Draw the left boarder
        canvas.drawLine(getDrawableArea().getLeft() + halfStrokeWidth, getDrawableArea().getTop(),
                getDrawableArea().getLeft() + halfStrokeWidth, getDrawableArea().getBottom(),
                paint);
        // Draw the right boader
        canvas.drawLine(getDrawableArea().getRight() - halfStrokeWidth, getDrawableArea().getTop(),
                getDrawableArea().getRight() - halfStrokeWidth, getDrawableArea().getBottom(),
                paint);
        // Draw the top boarder
        canvas.drawLine(getDrawableArea().getLeft(), getDrawableArea().getTop() + halfStrokeWidth,
                getDrawableArea().getRight(), getDrawableArea().getTop() + halfStrokeWidth, paint);
        // draw the bottom boader
        canvas.drawLine(getDrawableArea().getLeft(), getDrawableArea().getBottom() -
                        halfStrokeWidth,
                getDrawableArea().getRight(), getDrawableArea().getBottom() - halfStrokeWidth, paint);
    }

    /**
     * returns the stroke width of the boarder
     *
     * @return stroke width only
     */
    public int getStrokeWidth() {
        return mStrokeWidth;
    }
}
