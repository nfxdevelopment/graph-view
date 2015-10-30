package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * NFX Development
 * Created by nick on 25/10/15.
 */
public class Signal extends DrawableObject {

    private Paint mSignalPaint;

    private int mColor = Color.YELLOW;
    private float mStrokeWidth = 2f;

    Signal() {
        mSignalPaint = new Paint();
        mSignalPaint.setColor(mColor);
        mSignalPaint.setStrokeWidth(mStrokeWidth);
        mSignalPaint.setAntiAlias(true);
    }

    @Override
    public void doDraw(Canvas canvas) {
        canvas.drawLine(getDrawableArea().getLeft(), getDrawableArea().getHeight() / 2,
                getDrawableArea().getRight(), getDrawableArea().getHeight() / 2, mSignalPaint);
    }

    @Override
    public void calculateRemainingDrawableArea(DrawableArea currentDrawableArea) {

    }
}
