package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 */
public class LogYGridLines extends LogGridLines {

    public LogYGridLines(DrawableArea drawableArea, ZoomDisplay zoomDisplay) {
        super(drawableArea, zoomDisplay, AxisOrientation.yAxis);
    }

    @Override
    public void surfaceChange(DrawableArea drawableArea) {
        super.surfaceChange(drawableArea);

        maxLogValue = Math.log(mDrawableArea.getHeight());
    }

    @Override
    public void doDraw(Canvas canvas) {
        super.doDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(mGridColor);
        paint.setStrokeWidth(mGridStrokeWidth);

        for (int i = 0; i < mNumberOfGridLines; ++i) {
            float yIntersect = intersect(i);
            canvas.drawLine(mDrawableArea.getLeft(), mDrawableArea.getTop() + yIntersect,
                    mDrawableArea.getRight(), mDrawableArea.getTop() + yIntersect, paint);
        }
    }

    @Override
    public float intersect(int gridLine) {
        return intersect(gridLine, mDrawableArea.getHeight());
    }
}
