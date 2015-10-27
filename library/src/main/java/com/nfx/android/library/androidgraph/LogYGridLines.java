package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 */
public class LogYGridLines extends YGridLines {
    private double maxLogValue;

    public LogYGridLines(DrawableArea drawableArea) {
        super(drawableArea);
    }

    @Override
    public void surfaceChange(DrawableArea drawableArea) {
        super.surfaceChange(drawableArea);
        maxLogValue = Math.log(drawableArea.getHeight() - mGridStrokeWidth);
    }

    @Override
    public void doDraw(Canvas canvas) {
        super.doDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(mGridColor);
        paint.setStrokeWidth(mGridStrokeWidth);

        // This will ensure that we see all of the paint stoke
        float strokePadding = mGridStrokeWidth / 2f;
        // -1 due to drawing the surrounding frames along with the intersections
        // We also have to take the size of the line into account
        float spacing = ((float) mDrawableArea.getHeight() - mGridStrokeWidth) / (float)
                (mNumberOfGridLines - 1);

        for (int i = 0; i < mNumberOfGridLines; ++i) {
            float linearOffset = (spacing * (float) i);

            // Ensure we are not trying to find the log of 0 on first pass
            if (linearOffset == 0) {
                linearOffset = 1;
            }

            // Calculate the offset based on the log value / view width maximum, which will give
            // a percentage across the screen. use the percentage against the width to find the
            // exact screen position
            int offset = (int) ((Math.log(linearOffset) / maxLogValue) *
                    (double) (mDrawableArea.getHeight() - mGridStrokeWidth)) + (int) strokePadding;
            canvas.drawLine(mDrawableArea.getLeft(), mDrawableArea.getTop() + offset,
                    mDrawableArea.getRight(), mDrawableArea.getTop() + offset, paint);
        }
    }
}
