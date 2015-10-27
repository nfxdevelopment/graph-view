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
            int offset = (int) ((spacing * (float) i) + strokePadding);
            canvas.drawLine(mDrawableArea.getLeft(), mDrawableArea.getTop() + offset,
                    mDrawableArea.getRight(), mDrawableArea.getTop() + offset, paint);
        }
    }
}
