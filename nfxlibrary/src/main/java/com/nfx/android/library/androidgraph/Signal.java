package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Color;

import com.nfx.android.library.androidgraph.AxisScale.AxisParameters;
import com.nfx.android.library.androidgraph.AxisScale.GraphParameters;

/**
 * NFX Development
 * Created by nick on 25/10/15.
 * <p/>
 * This object will draw a signal on screen. The object has the ability to draw either in a
 * logarithmic or linear fashion at runtime
 */
class Signal extends DrawableObject {
    /**
     * The buffer which will be drawn by this object
     */
    private final SignalBufferInterface mSignalBufferInterface;
    /**
     * x axis zoom to scale to
     */
    private final ZoomDisplay mXZoomDisplay;
    /**
     * Parent graph View
     */
    private final GraphParameters mGraphParameters;
    /**
     * Drawing buffer
     */
    private float[] mDrawBuffer;

    /**
     * Constructor
     *
     * @param signalBufferInterface the buffer to be drawn
     * @param graphParameters       axis parameters of the current graph
     * @param xZoomDisplay          zoom object for the x axis
     */
    Signal(GraphParameters graphParameters, SignalBufferInterface
            signalBufferInterface, ZoomDisplay xZoomDisplay) {
        this.mGraphParameters = graphParameters;
        this.mSignalBufferInterface = signalBufferInterface;
        this.mXZoomDisplay = xZoomDisplay;

        int mColor = Color.YELLOW;
        mPaint.setColor(mColor);
        float mStrokeWidth = 4f;
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setAntiAlias(true);
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
        AxisParameters xAxisParameters = mGraphParameters.getXAxisParameters();

        float lowerX = xAxisParameters.getMinimumValue() +
                (xAxisParameters.getAxisSpan() * mXZoomDisplay.getDisplayOffsetPercentage());
        float higherX = xAxisParameters.getMinimumValue() +
                (xAxisParameters.getAxisSpan() * mXZoomDisplay.getFarSideOffsetPercentage());

        mSignalBufferInterface.getScaledBuffer(mDrawBuffer, lowerX, higherX, xAxisParameters);
        int drawBufferLength = mDrawBuffer.length;

        float spacing = screenWidth / (float) (drawBufferLength - 1);

        for(int i = 0; i < (drawBufferLength - 1); i++) {
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

                // Check if samples are outside of screen range
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
                        getDrawableArea().checkLimitY(drawEndPosY), mPaint);
            }
        }
    }

    @Override
    public void surfaceChanged(DrawableArea drawableArea) {
        super.surfaceChanged(drawableArea);
        /*
      How many points per on screen buffer. This is a screen width divisor
     */
        int mLineResolution = 4;
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

    /**
     * @return the interface to the signal buffer
     */
    SignalBufferInterface getSignalBufferInterface() {
        return mSignalBufferInterface;
    }

}
