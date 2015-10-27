package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 */
public class LinYGridLines extends YGridLines {
    public LinYGridLines(DrawableArea drawableArea) {
        super(drawableArea);
    }

    @Override
    public float yIntersect(int gridLine) {
        // This will ensure that we see all of the paint stoke
        float strokePadding = mGridStrokeWidth / 2f;
        // -1 due to drawing the surrounding frames along with the intersections
        // We also have to take the size of the line into account
        float spacing = ((float) heightOfViewInsideGridStoke) / (float) (mNumberOfGridLines - 1);
        return ((spacing * (float) gridLine) + strokePadding);
    }

    @Override
    public void doDraw(Canvas canvas) {
        super.doDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(mGridColor);
        paint.setStrokeWidth(mGridStrokeWidth);

        // This will ensure that we see all of the paint stoke
        float strokePadding = mGridStrokeWidth / 2f;

        for (int i = 0; i < mNumberOfGridLines; ++i) {
            canvas.drawLine(mDrawableArea.getLeft(), mDrawableArea.getTop() + yIntersect(i),
                    mDrawableArea.getRight(), mDrawableArea.getTop() + yIntersect(i), paint);
        }
    }
}
