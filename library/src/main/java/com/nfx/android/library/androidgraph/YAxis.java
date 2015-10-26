package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * NFX Development
 * Created by nick on 25/10/15.
 */
public class YAxis extends Axis {
    public YAxis(DrawableArea drawableArea) {
        super(drawableArea);
    }

    @Override
    public void doDraw(Canvas canvas) {
        super.doDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(mGridColor);
        paint.setStrokeWidth(mGridStrokeWidth);

        int leftAlignStrokeWidthCompensate = mDrawableArea.getLeft() + (int) (mGridStrokeWidth /
                2f);
        canvas.drawLine(leftAlignStrokeWidthCompensate, mDrawableArea.getTop(),
                leftAlignStrokeWidthCompensate, mDrawableArea.getBottom(), paint);
    }
}
