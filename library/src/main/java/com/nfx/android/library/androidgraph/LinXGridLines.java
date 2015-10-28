package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 */
public class LinXGridLines extends LinGridLines {
    public LinXGridLines(DrawableArea drawableArea, ZoomDisplay zoomDisplay) {
        super(drawableArea, zoomDisplay, AxisOrientation.xAxis);
    }

    @Override
    public void doDraw(Canvas canvas) {
        super.doDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(mGridColor);
        paint.setStrokeWidth(mGridStrokeWidth);

        for (int i = 0; i < mNumberOfGridLines; ++i) {
            float xIntersect = intersect(i);
            canvas.drawLine(mDrawableArea.getLeft() + xIntersect, mDrawableArea.getTop(),
                    mDrawableArea.getLeft() + xIntersect, mDrawableArea.getBottom(), paint);
        }
    }

    @Override
    public float intersect(int gridLine) {
        return intersect(gridLine, mDrawableArea.getWidth());
    }
}
