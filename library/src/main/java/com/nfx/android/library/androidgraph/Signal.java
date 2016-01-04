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
    @SuppressWarnings("unused")
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
    private final int mLineResolution = 4;
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
        float screenHeight = (float) getDrawableArea().getHeight();
        float screenWidth = (float) getDrawableArea().getWidth();
        float screenTop = (float) getDrawableArea().getTop();

        mSignalBuffer.getScaledBuffer(mDrawBuffer);

        float spacing = screenWidth / (float) (mDrawBuffer.length - 1);

        // TODO Look at optimising the following
        for(int i = 0; i < (((int) screenWidth / mLineResolution) - 1); i++) {
            float startPosY = mDrawBuffer[i];
            float endPosY = mDrawBuffer[i + 1];

            // ensure that at least part of the line will be visible on screen
            if((startPosY < 1f || endPosY < 1f) &&
                    (startPosY > 0f || endPosY > 0f)) {

                float startPosX = (float) i;
                float endPosX = (float) (i + 1);

                float gradient = (endPosY - startPosY) / (endPosX - startPosX);
                // y = gradient*X + yIntercept
                // yIntercept = y - gradient*X
                float yIntercept = startPosY - gradient * startPosX;

                // Both samples are within the Screen dimensions
                if(startPosY > 1f) {
                    // startPosY = 1
                    // 1 = gradient*x + yIntercept
                    // 1 - yIntercept / gradient = x
                    startPosX = (1f - yIntercept) / gradient;
                    startPosY = 1f;
                } else if(startPosY < 0f) {
                    // startPosY = 0
                    // 1 = gradient*x + yIntercept
                    // -yIntercept/gradient = x
                    startPosX = -yIntercept / gradient;
                    startPosY = 0f;
                }
                if(endPosY > 1f) {
                    // endPosY = 1
                    // 1 = gradient*x + yIntercept
                    // 1 - yIntercept / gradient = x
                    endPosX = (1f - yIntercept) / gradient;
                    endPosY = 1f;
                } else if(endPosY < 0f) {
                    // endPosY = 0
                    // 1 = gradient*x + yIntercept
                    // -yIntercept/gradient = x
                    endPosX = -yIntercept / gradient;
                    endPosY = 0f;
                }

                float drawStartPosY = screenTop + screenHeight - (screenHeight * startPosY);
                float drawStartPosX = screenLeft + (spacing * startPosX);
                float drawEndPosY = screenTop + screenHeight - (screenHeight * endPosY);
                float drawEndPosX = screenLeft + (spacing * endPosX);

                canvas.drawLine(getDrawableArea().checkLimitX(drawStartPosX),
                        getDrawableArea().checkLimitY(drawStartPosY),
                        getDrawableArea().checkLimitX(drawEndPosX),
                        getDrawableArea().checkLimitY(drawEndPosY), mSignalPaint);
            }
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
