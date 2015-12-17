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
    private final SignalBuffer mSignalBuffer;

    /**
     * Style of the signal
     */
    private final Paint mSignalPaint;

    /**
     * How many points per on screen buffer. This is a screen width divisor
     */
    private final int mLineResolution = 16;
    /**
     * Drawing buffer
     */
    private float[] mDrawBuffer;

    /**
     * Constructor
     *
     * @param signalBuffer the buffer to be drawn
     */
    Signal(SignalBuffer signalBuffer) {
        mSignalBuffer = signalBuffer;

        mSignalPaint = new Paint();
        /*
      Default color
     */
        int mColor = Color.YELLOW;
        mSignalPaint.setColor(mColor);
        /*
      Default stroke width
     */
        float mStrokeWidth = 4f;
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
        float screenLeft = (float) getDrawableArea().getLeft();
        float screenTop = (float) getDrawableArea().getTop();
        float screenHeight = (float) getDrawableArea().getHeight();
        float screenWidth = (float) getDrawableArea().getWidth();

        mSignalBuffer.getScaledBuffer(mDrawBuffer);

        float spacing = screenWidth / (float) (mDrawBuffer.length - 1);

        for(int i = 0; i < (((int) screenWidth / mLineResolution) - 1); i++) {
            float startPosY = screenTop + (screenHeight * mDrawBuffer[i]);
            float startPosX = screenLeft + (spacing * (float) i);
            float endPosY = screenTop + (screenHeight * mDrawBuffer[i + 1]);
            float endPosX = screenLeft + (spacing * (float) (i + 1));

            canvas.drawLine(getDrawableArea().checkLimitX(startPosX),
                    getDrawableArea().checkLimitY(startPosY),
                    getDrawableArea().checkLimitX(endPosX),
                    getDrawableArea().checkLimitY(endPosY), mSignalPaint);
        }
    }

    @Override
    public void surfaceChanged(DrawableArea drawableArea) {
        super.surfaceChanged(drawableArea);
        mDrawBuffer = new float[getDrawableArea().getWidth() / mLineResolution];
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
