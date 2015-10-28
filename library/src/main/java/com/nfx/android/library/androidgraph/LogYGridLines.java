package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 */
public class LogYGridLines extends YGridLines {
    private double maxLogValue;

    public LogYGridLines(DrawableArea drawableArea, ZoomDisplay zoomDisplay) {
        super(drawableArea, zoomDisplay);
    }

    @Override
    public void surfaceChange(DrawableArea drawableArea) {
        super.surfaceChange(drawableArea);

        maxLogValue = Math.log(heightOfViewInsideGridStoke);
    }

    @Override
    public float yIntersect(int gridLine) {
        // This will ensure that we see all of the paint stoke
        float strokePadding = mGridStrokeWidth / 2f;
        // -1 due to drawing the surrounding frames along with the intersections
        // We also have to take the size of the line into account
        float spacing = ((float) heightOfViewInsideGridStoke) / (float) (mNumberOfGridLines - 1);
        float linearOffset = ((spacing * (float) gridLine));

        // Ensure we are not trying to find the log of 0 on first pass
        if (linearOffset == 0) {
            linearOffset = 1;
        }

        return (float) ((Math.log(linearOffset) / maxLogValue) *
                (float) (heightOfViewInsideGridStoke)) + strokePadding;
    }

    @Override
    public void doDraw(Canvas canvas) {
        super.doDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(mGridColor);
        paint.setStrokeWidth(mGridStrokeWidth);

        for (int i = 0; i < mNumberOfGridLines; ++i) {
            canvas.drawLine(mDrawableArea.getLeft(), mDrawableArea.getTop() + yIntersect(i),
                    mDrawableArea.getRight(), mDrawableArea.getTop() + yIntersect(i), paint);
        }
    }
}
