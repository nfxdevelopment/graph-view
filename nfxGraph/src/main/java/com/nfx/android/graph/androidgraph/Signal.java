package com.nfx.android.graph.androidgraph;

import android.graphics.Canvas;
import android.graphics.Color;

import com.nfx.android.graph.androidgraph.AxisScale.AxisParameters;
import com.nfx.android.graph.androidgraph.AxisScale.GraphParameters;

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
     * As more than one point can represent multiple values a minimum and maximum buffer is used
     */
    private float[] mDrawBufferMinimumValues;
    private float[] mDrawBufferMaximumValues;
    /**
     * Stroke width of line
     */
    private float mStrokeWidth = 4f;

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
        AxisParameters xAxisParameters = mGraphParameters.getXAxisParameters();

        float lowerX = xAxisParameters.getMinimumValue() +
                (xAxisParameters.getAxisSpan() * mXZoomDisplay.getDisplayOffsetPercentage());
        float higherX = xAxisParameters.getMinimumValue() +
                (xAxisParameters.getAxisSpan() * mXZoomDisplay.getFarSideOffsetPercentage());

        mSignalBufferInterface.getScaledMinimumMaximumBuffers(mDrawBufferMinimumValues,
                mDrawBufferMaximumValues, lowerX, higherX, xAxisParameters);

        int drawBufferLength = mDrawBufferMinimumValues.length;

        for(int i = 0; i < (drawBufferLength - 1); i++) {
            float minimumY = mDrawBufferMinimumValues[i];
            float maximumY = mDrawBufferMaximumValues[i];
            float nextMinimumY = mDrawBufferMinimumValues[i + 1];
            float nextMaximumY = mDrawBufferMaximumValues[i + 1];

            // Check if samples are outside of screen range
            minimumY = checkInBounds(minimumY);
            maximumY = checkInBounds(maximumY);
            nextMinimumY = checkInBounds(nextMinimumY);
            nextMaximumY = checkInBounds(nextMaximumY);

            if(minimumY >= nextMinimumY && maximumY >= nextMaximumY && minimumY >= nextMaximumY) {
                // If the values would draw an ascending line
                doDrawLine(canvas, maximumY, nextMinimumY, i, drawBufferLength);
            } else if(minimumY <= nextMinimumY && maximumY <= nextMaximumY && maximumY <=
                    nextMaximumY) {
                // If the values would draw an descending line
                doDrawLine(canvas, minimumY, nextMaximumY, i, drawBufferLength);
            } else {
                // If no line can be drawn just draw a rect of the current value
                float screenLeft = (float) getDrawableArea().getLeft();
                float screenHeight = (float) getDrawableArea().getHeight();
                float screenWidth = (float) getDrawableArea().getWidth();
                float screenTop = (float) getDrawableArea().getTop();

                float spacing = screenWidth / (float) (drawBufferLength - 1);

                float bottom = screenTop + screenHeight - (screenHeight * minimumY);
                float left = screenLeft + (spacing * i);
                float top = screenTop + screenHeight - (screenHeight * maximumY);
                float right = screenLeft + (spacing * (i + 1));

                // Ensure something can be seen
                float centre = bottom - top;
                if(centre < mStrokeWidth) {
                    bottom = bottom + (centre / 2) + (mStrokeWidth / 2);
                    top = top - (centre / 2) - (mStrokeWidth / 2);
                }

                canvas.drawRect(getDrawableArea().checkLimitX(left),
                        getDrawableArea().checkLimitY(top),
                        getDrawableArea().checkLimitX(right),
                        getDrawableArea().checkLimitY(bottom), mPaint);
            }
        }
    }

    /**
     * Draw a straight graph line from startY to endY. The x dimensions are calculated from
     * bufferIndex to bufferIndex+1
     *
     * @param canvas       canvas to draw onto
     * @param startY       starting position on Y axis
     * @param endY         end position on Y axis
     * @param bufferIndex  index drawing in buffer
     * @param bufferLength length of buffer
     */
    private void doDrawLine(Canvas canvas, float startY, float endY, int bufferIndex,
                            int bufferLength) {
        // If both positions are off screen do not try and draw
        if((startY < 1f || endY < 1f) &&
                (startY > 0f || endY > 0f)) {
            float screenLeft = (float) getDrawableArea().getLeft();
            float screenHeight = (float) getDrawableArea().getHeight();
            float screenWidth = (float) getDrawableArea().getWidth();
            float screenTop = (float) getDrawableArea().getTop();

            float spacing = screenWidth / (float) (bufferLength - 1);

            float startX = (float) bufferIndex;
            float endX = (float) (bufferIndex + 1);

            float gradient = (endY - startY) / (endX - startX);
            // y = gradient*X + yIntercept
            // yIntercept = y - gradient*X
            float yIntercept = startY - gradient * startX;

            // Check if samples are outside of screen range
            if(startY > 1f) {
                // startY = 1
                // 1 = gradient*x + yIntercept
                // 1 - yIntercept / gradient = x
                startX = (1f - yIntercept) / gradient;
                startY = 1f;
            } else if(startY < 0f) {
                // startY = 0
                // 1 = gradient*x + yIntercept
                // -yIntercept/gradient = x
                startX = -yIntercept / gradient;
                startY = 0f;
            }
            if(endY > 1f) {
                // endY = 1
                // 1 = gradient*x + yIntercept
                // 1 - yIntercept / gradient = x
                endX = (1f - yIntercept) / gradient;
                endY = 1f;
            } else if(endY < 0f) {
                // endY = 0
                // 1 = gradient*x + yIntercept
                // -yIntercept/gradient = x
                endX = -yIntercept / gradient;
                endY = 0f;
            }

            float drawStartPosY = screenTop + screenHeight - (screenHeight * startY);
            float drawStartPosX = screenLeft + (spacing * startX);
            float drawEndPosY = screenTop + screenHeight - (screenHeight * endY);
            float drawEndPosX = screenLeft + (spacing * endX);

            canvas.drawLine(getDrawableArea().checkLimitX(drawStartPosX),
                    getDrawableArea().checkLimitY(drawStartPosY),
                    getDrawableArea().checkLimitX(drawEndPosX),
                    getDrawableArea().checkLimitY(drawEndPosY), mPaint);
        }
    }

    /**
     * Check and return a value that is within the bounds of 0 - 1
     *
     * @param value value to check
     * @return a value within the bounds
     */
    private float checkInBounds(float value) {
        if(value > 1f) {
            value = 1f;
        } else if(value < 0f) {
            value = 0f;
        }
        return value;
    }

    @Override
    public void surfaceChanged(DrawableArea drawableArea) {
        super.surfaceChanged(drawableArea);
        /*
      How many points per on screen buffer. This is a screen width divisor
     */
        int mLineResolution = 4;
        mDrawBufferMinimumValues = new float[getDrawableArea().getWidth() / mLineResolution];
        mDrawBufferMaximumValues = new float[getDrawableArea().getWidth() / mLineResolution];
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
