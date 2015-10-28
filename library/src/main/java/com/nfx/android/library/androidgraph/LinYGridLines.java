package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 */
public class LinYGridLines extends YGridLines {
    public LinYGridLines(DrawableArea drawableArea, ZoomDisplay zoomDisplay) {
        super(drawableArea, zoomDisplay);
    }

    /**
     * Gives the value of where a grid line will interest y on the screen
     *
     * @param gridLine grid line to find, base 0
     * @return the y Intersect or -1 if the grid line is out of range
     */
    @Override
    public float yIntersect(int gridLine) {
        if (gridLine >= mNumberOfGridLines || gridLine < 0) {
            return -1f;
        }

        // +1 as there would be mNumberOfGridLines intersecting the graph which splits
        // mNumberOfGridLines + 1 areas
        float spacing = (float) mDrawableArea.getHeight() / (float) (mNumberOfGridLines + 1);
        // The first line lies at on the area boundary of the first block hence +1
        return spacing * (float) (gridLine + 1);
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
