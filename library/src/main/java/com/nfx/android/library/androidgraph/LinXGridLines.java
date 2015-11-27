package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 *
 * Draws linear style grid lines in the x plane. Call doDraw with the canvas in which to draw on
 */
public class LinXGridLines extends LinGridLines {
    /**
     * Constructor
     */
    public LinXGridLines() {
        super(AxisOrientation.xAxis);
    }

    /**
     * Draws all lines which are viewable on screen
     *
     * @param canvas a canvas to draw onto
     */
    @Override
    public void doDraw(Canvas canvas) {
        super.doDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(mGridColor);
        paint.setStrokeWidth(mGridStrokeWidth);

        for (int i = 0; i < mNumberOfGridLines; ++i) {
            float xIntersect = intersectZoomCompensated(i);
            if (xIntersect >= 0) {
                canvas.drawLine(getDrawableArea().getLeft() + xIntersect, getDrawableArea()
                        .getTop(),
                        getDrawableArea().getLeft() + xIntersect, getDrawableArea().getBottom(),
                        paint);
            }
        }
    }

    @Override
    public void showAxisText(Context context, float minimumValue, float maximumValue) {
        super.showAxisText(context, minimumValue, maximumValue);
        mAxisText = new XAxisText(context, this, minimumValue, maximumValue);
    }

    /**
     * calls the dimension specific intersectZoomCompensated workout
     * @param gridLine grid line to find out the intersecting value
     * @return value where line intersects
     */
    @Override
    public float intersectZoomCompensated(int gridLine) {
        return intersectZoomCompensated(gridLine, getDrawableArea().getWidth());
    }

    /**
     * The surface size has changed update the current object to resize drawing
     *
     * @param drawableArea new surface size
     */
    public void surfaceChanged(DrawableArea drawableArea) {
        super.surfaceChanged(drawableArea);
        setGridLinesSize(drawableArea.getWidth());
        setGridLinesOffset(0);
    }
}
