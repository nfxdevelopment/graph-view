package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * NFX Development
 * Created by nick on 25/10/15.
 *
 * This object will draw a signal on screen. The object has the ability to draw either in a
 * logarithmic or linear fashion at runtime
 */
public class Signal extends DrawableObject {
    private static final String TAG = "Signal";

    private SignalBuffer mSignalBuffer;

    private Paint mSignalPaint;

    private int mColor = Color.YELLOW;
    private float mStrokeWidth = 2f;

    private int mLineResolution = 16;

    Signal(SignalBuffer signalBuffer) {
        mSignalBuffer = signalBuffer;

        mSignalPaint = new Paint();
        mSignalPaint.setColor(mColor);
        mSignalPaint.setStrokeWidth(mStrokeWidth);
        mSignalPaint.setAntiAlias(true);
    }

    @Override
    public void doDraw(Canvas canvas) {
        if (mSignalBuffer.getSignalScale() == SignalBuffer.SignalScale.linear) {
            doDrawLinear(canvas);
        } else {
            doDrawLogarithmic(canvas);
        }
    }

    private void doDrawLinear(Canvas canvas) {
        float[] buffer = mSignalBuffer.getScaledBuffer(getDrawableArea().getWidth() /
                mLineResolution);

        float spacing = (float) getDrawableArea().getWidth() / (float) (buffer.length - 1);

        for (int i = 0; i < (getDrawableArea().getWidth() / mLineResolution) - 1; i++) {
            float startPosY = (float) getDrawableArea().getTop() +
                    ((float) getDrawableArea().getHeight() * buffer[i]);
            float startPosX = (float) getDrawableArea().getLeft() + (spacing * (float) i);
            float endPosY = (float) getDrawableArea().getTop() +
                    ((float) getDrawableArea().getHeight() * buffer[i + 1]);
            float endPosX = (float) getDrawableArea().getLeft() + (spacing * (float) (i + 1));

            canvas.drawLine(startPosX, startPosY, endPosX, endPosY, mSignalPaint);
        }
    }

    private void doDrawLogarithmic(Canvas canvas) {
        float[] buffer = mSignalBuffer.getScaledBuffer(getDrawableArea().getWidth() / mLineResolution);

        float screenWidth = (float) getDrawableArea().getWidth();

        float spacing = screenWidth / (float) (buffer.length - 1);
        double maxLogValue = Math.log(screenWidth);

        for (int i = 0; i < (getDrawableArea().getWidth() / mLineResolution) - 1; i++) {
            float startPosY = (float) getDrawableArea().getTop() +
                    ((float) getDrawableArea().getHeight() * buffer[i]);

            float startPosXLinear = (spacing * (float) i);
            // This i because the first value will always be 0, as we do not want to lMath.log 0
            if (startPosXLinear == 0) startPosXLinear = 1;
            float startPosX = getDrawableArea().getLeft() +
                    ((float) Math.log(startPosXLinear) / (float) maxLogValue) * screenWidth;

            float endPosY = (float) getDrawableArea().getTop() +
                    ((float) getDrawableArea().getHeight() * buffer[i + 1]);

            float endPosXLinear = spacing * (float) (i + 1);
            float endPosX = getDrawableArea().getLeft() +
                    ((float) Math.log(endPosXLinear) / (float) maxLogValue) * screenWidth;

            canvas.drawLine(startPosX, startPosY, endPosX, endPosY, mSignalPaint);
        }
    }

    @Override
    public void calculateRemainingDrawableArea(DrawableArea currentDrawableArea) {

    }
}
