package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 */
public class LinXGridLines extends LinGridLines {
    public LinXGridLines(ZoomDisplay zoomDisplay) {
        super(zoomDisplay, AxisOrientation.xAxis);
    }

    @Override
    public void doDraw(Canvas canvas) {
        super.doDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(mGridColor);
        paint.setStrokeWidth(mGridStrokeWidth);

        for (int i = 0; i < mNumberOfGridLines; ++i) {
            float xIntersect = intersect(i);
            canvas.drawLine(getDrawableArea().getLeft() + xIntersect, getDrawableArea().getTop(),
                    getDrawableArea().getLeft() + xIntersect, getDrawableArea().getBottom(), paint);
        }
    }

    @Override
    public float intersect(int gridLine) {
        return intersect(gridLine, getDrawableArea().getWidth());
    }
}
