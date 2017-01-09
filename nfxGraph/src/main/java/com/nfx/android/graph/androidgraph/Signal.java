package com.nfx.android.graph.androidgraph;

import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;

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
    private final SignalBufferInterface signalBufferInterface;
    /**
     * x axis zoom to scale to
     */
    private final ZoomDisplay xZoomDisplay;
    /**
     * Parent graph View
     */
    private final GraphParameters graphParameters;
    /**
     * Stroke width of line
     */
    private final float strokeWidth = 4f;
    /**
     * As more than one point can represent multiple values a minimum and maximum buffer is used
     */
    private float[] drawBufferMinimumValues;
    private float[] drawBufferMaximumValues;
    /**
     * A line and point to show where the zero intercept of y is
     */
    @Nullable
    private LabelPointer yAxisZeroIntercept = null;

    /**
     * Constructor
     *
     * @param signalBufferInterface the buffer to be drawn
     * @param graphParameters       axis parameters of the current graph
     * @param xZoomDisplay          zoom object for the x axis
     */
    Signal(GraphParameters graphParameters, SignalBufferInterface
            signalBufferInterface, ZoomDisplay xZoomDisplay) {
        this.graphParameters = graphParameters;
        this.signalBufferInterface = signalBufferInterface;
        this.xZoomDisplay = xZoomDisplay;

        int mColor = Color.YELLOW;
        paint.setColor(mColor);
        paint.setStrokeWidth(strokeWidth);
        paint.setAntiAlias(true);
    }

    /**
     * Call to draw the signal on screen
     *
     * @param canvas a canvas to draw onto
     */
    @Override
    public void doDraw(Canvas canvas) {
        AxisParameters xAxisParameters = graphParameters.getXAxisParameters();

        float lowerX = xAxisParameters.getMinimumValue() +
                (xAxisParameters.getAxisSpan() * xZoomDisplay.getDisplayOffsetPercentage());
        float higherX = xAxisParameters.getMinimumValue() +
                (xAxisParameters.getAxisSpan() * xZoomDisplay.getFarSideOffsetPercentage());

        signalBufferInterface.getScaledMinimumMaximumBuffers(drawBufferMinimumValues,
                drawBufferMaximumValues, lowerX, higherX, xAxisParameters);

        int drawBufferLength = drawBufferMinimumValues.length;

        for(int i = 0; i < (drawBufferLength - 1); i++) {
            float minimumY = drawBufferMinimumValues[i];
            float maximumY = drawBufferMaximumValues[i];
            float nextMinimumY = drawBufferMinimumValues[i + 1];
            float nextMaximumY = drawBufferMaximumValues[i + 1];

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
                if(centre < strokeWidth) {
                    bottom = bottom + (centre / 2) + (strokeWidth / 2);
                    top = top - (centre / 2) - (strokeWidth / 2);
                }

                canvas.drawRect(getDrawableArea().checkLimitX(left),
                        getDrawableArea().checkLimitY(top),
                        getDrawableArea().checkLimitX(right),
                        getDrawableArea().checkLimitY(bottom), paint);
            }
        }

        if(yAxisZeroIntercept != null) {
            yAxisZeroIntercept.doDraw(canvas);
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
                    getDrawableArea().checkLimitY(drawEndPosY), paint);
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
        drawBufferMinimumValues = new float[getDrawableArea().getWidth() / mLineResolution];
        drawBufferMaximumValues = new float[getDrawableArea().getWidth() / mLineResolution];

        if(yAxisZeroIntercept != null) {
            yAxisZeroIntercept.surfaceChanged(drawableArea);
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

    /**
     * @return the interface to the signal buffer
     */
    SignalBufferInterface getSignalBufferInterface() {
        return signalBufferInterface;
    }

    /**
     * Enable the line and pointer for the zero intercept of the y axis
     */
    void enableYAxisZeroIntercept(int colour) {
        yAxisZeroIntercept = new HorizontalLabelPointer(signalBufferInterface.getYZoomDisplay());
        yAxisZeroIntercept.surfaceChanged(getDrawableArea());
        yAxisZeroIntercept.setColour(colour);
        yAxisZeroIntercept.setShowLine(true);
    }

    void disableYAxisZeroIntercept() {
        yAxisZeroIntercept = null;
    }
}
