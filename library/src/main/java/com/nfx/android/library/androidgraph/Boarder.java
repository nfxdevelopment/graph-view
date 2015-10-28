package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * NFX Development
 * Created by nick on 28/10/15.
 */
public class Boarder extends DrawableObject {

    /**
     * Stroke width of the board line
     */
    private static final int strokeWidth = 4;
    /**
     * Color of the boarder this can be updated at runtime
     */
    private int color = Color.GRAY;

    /**
     * @param drawableArea the area in which the boarder can be drawn
     */
    public Boarder(DrawableArea drawableArea) {
        super(drawableArea);
    }

    /**
     * This will draw 4 lines around the outer edge of the viewable area.
     * It will also take into account the stroke width to ensure the whole line is viewable
     *
     * @param canvas draw directly onto this canvas
     */
    @Override
    public void doDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);

        int halfStrokeWidth = strokeWidth / 2;

        // Draw the left boarder
        canvas.drawLine(mDrawableArea.getLeft() + halfStrokeWidth, mDrawableArea.getTop(),
                mDrawableArea.getLeft() + halfStrokeWidth, mDrawableArea.getBottom(), paint);
        // Draw the right boader
        canvas.drawLine(mDrawableArea.getRight() - halfStrokeWidth, mDrawableArea.getTop(),
                mDrawableArea.getRight() - halfStrokeWidth, mDrawableArea.getBottom(), paint);
        // Draw the top boarder
        canvas.drawLine(mDrawableArea.getLeft(), mDrawableArea.getTop() + halfStrokeWidth,
                mDrawableArea.getRight(), mDrawableArea.getTop() + halfStrokeWidth, paint);
        // draw the bottom boader
        canvas.drawLine(mDrawableArea.getLeft(), mDrawableArea.getBottom() - halfStrokeWidth,
                mDrawableArea.getRight(), mDrawableArea.getBottom() - halfStrokeWidth, paint);
    }
}
