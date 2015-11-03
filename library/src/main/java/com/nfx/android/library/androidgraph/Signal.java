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

    /**
     * The buffer which will be drawn by this object
     */
    private SignalBuffer mSignalBuffer;

    /**
     * Style of the signal
     */
    private Paint mSignalPaint;

    /**
     * Default color
     */
    private int mColor = Color.YELLOW;
    /**
     * Default stroke width
     */
    private float mStrokeWidth = 2f;

    /**
     * How many points per on screen buffer. This is a screen width divisor
     */
    private int mLineResolution = 16;

    /**
     * Constructor
     *
     * @param signalBuffer the buffer to be drawn
     */
    Signal(SignalBuffer signalBuffer) {
        mSignalBuffer = signalBuffer;

        mSignalPaint = new Paint();
        mSignalPaint.setColor(mColor);
        mSignalPaint.setStrokeWidth(mStrokeWidth);
        mSignalPaint.setAntiAlias(true);
    }

    /**
     * Call to draw the signal on screen
     *
     * @param canvas a canvas to draw onto
     */
    @Override
    public void doDraw(Canvas canvas) {
        if (mSignalBuffer.getSignalScale() == SignalBuffer.SignalScale.linear) {
            doDrawLinear(canvas);
        } else {
            doDrawLogarithmic(canvas);
        }
    }

    /**
     * Called when the signal is to be drawn linearly
     *
     * @param canvas a canvas to draw onto
     */
    private void doDrawLinear(Canvas canvas) {
        float screenLeft = (float) getDrawableArea().getLeft();
        float screenTop = (float) getDrawableArea().getTop();
        float screenHeight = (float) getDrawableArea().getHeight();
        float screenWidth = (float) getDrawableArea().getWidth();

        float[] buffer = mSignalBuffer.getScaledBuffer((int) screenWidth / mLineResolution);

        float spacing = screenWidth / (float) (buffer.length - 1);

        for (int i = 0; i < (((int) screenWidth / mLineResolution) - 1); i++) {
            float startPosY = screenTop + (screenHeight * buffer[i]);
            float startPosX = screenLeft + (spacing * (float) i);
            float endPosY = screenTop + (screenHeight * buffer[i + 1]);
            float endPosX = screenLeft + (spacing * (float) (i + 1));

            canvas.drawLine(startPosX, startPosY, endPosX, endPosY, mSignalPaint);
        }
    }

    /**
     * Called when the signal is to be drawn logarithmically
     *
     * @param canvas a canvas to draw onto
     */
    private void doDrawLogarithmic(Canvas canvas) {
        float screenLeft = (float) getDrawableArea().getLeft();
        float screenTop = (float) getDrawableArea().getTop();
        float screenHeight = (float) getDrawableArea().getHeight();
        float screenWidth = (float) getDrawableArea().getWidth();

        float[] buffer = mSignalBuffer.getScaledBuffer((int) screenWidth / mLineResolution);

        float spacing = screenWidth / (float) (buffer.length - 1);
        double maxLogValue = Math.log(screenWidth);

        for (int i = 0; i < (screenWidth / mLineResolution) - 1; i++) {
            float startPosY = screenTop + (screenHeight * buffer[i]);

            float startPosXLinear = (spacing * (float) i);
            // This i because the first value will always be 0, as we do not want to lMath.log 0
            if (startPosXLinear == 0) startPosXLinear = 1;
            float startPosX = screenLeft +
                    ((float) Math.log(startPosXLinear) / (float) maxLogValue) * screenWidth;

            float endPosY = screenTop + (screenHeight * buffer[i + 1]);

            float endPosXLinear = spacing * (float) (i + 1);
            float endPosX = screenLeft +
                    ((float) Math.log(endPosXLinear) / (float) maxLogValue) * screenWidth;

            canvas.drawLine(startPosX, startPosY, endPosX, endPosY, mSignalPaint);
        }
    }

    /**
     * The signal can be drawn over, therefore theoretically it takes up no screen space
     *
     * @param currentDrawableArea the drawable area canvas to calculate the area taken
     */
    @Override
    public void calculateRemainingDrawableArea(DrawableArea currentDrawableArea) {

    }
}
