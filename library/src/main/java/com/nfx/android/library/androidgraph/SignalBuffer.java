package com.nfx.android.library.androidgraph;

import android.util.Log;

/**
 * NFX Development
 * Created by nick on 31/10/15.
 * <p/>
 * A signal buffer holds a buffer with additional information on how it should be displayed on
 * screen. This can be used to pass buffer information between graphical and input objects.
 */
public class SignalBuffer {
    private static final String TAG = "SignalBuffer";
    /**
     * Scaled for the y axis ranging from 0 - 1 where 1 is unscaled
     */
    private float mYScale = 1f;

    /**
     * Offset based in the y axis no offset = 0
     */
    private float mYOffset = 0f;
    /**
     * Offset based in the x axis no offset = 0
     */
    private float mXOffset = 0f;
    /**
     * A scale that is applied to all buffers encompassed in this object
     */
    private float mXScale = 1f;
    /**
     * So we now if the data is logarithmic or linear
     */
    private SignalScale mSignalScale;

    /**
     * Unique Id for signal
     */
    private int mId;

    /**
     * buffer of given size which is worked out at runtime. This data is normalized 0-1
     */
    private float[] mBuffer;

    SignalBuffer(int id, int sizeOfBuffer, SignalScale signalScale) {
        mId = id;
        mBuffer = new float[sizeOfBuffer];
        mSignalScale = signalScale;
    }

    private float[] getBuffer() {
        synchronized (this) {
            return mBuffer;
        }
    }

    /**
     * Sets the member buffer. Please ensure data is normalised to 0-1 before setting. If the
     * buffer passed in does not match the size of the member buffer. It will not be set and a
     * Log warning is displayed.
     *
     * @param buffer source for buffer copy
     */
    public void setBuffer(float[] buffer) {
        synchronized (this) {
            if (mBuffer.length == buffer.length) {
                System.arraycopy(buffer, 0, mBuffer, 0, mBuffer.length);
            } else {
                Log.w(TAG, "Buffer passed in does not match size of signal buffer");
            }
        }
    }

    /**
     * This will return a buffer with the desired {@code numberOfPoints} size. Essentially scaling
     * scaling the read buffer
     * The algorithm works out each new point separately. More often than not the new sample will
     * fall between to of buffer points therefore we have to do some aliasing
     *
     * @param numberOfPoints desired number of points for new buffer
     * @return new buffer
     */
    public float[] getScaledBuffer(int numberOfPoints) {

        float[] scaledBuffer = new float[numberOfPoints];

        synchronized (this) {
            // -1 to get the spacing between the samples.
            float spacing = mXScale / (numberOfPoints - 1);

            for (int i = 0; i < numberOfPoints; i++) {
                float pointOffsetPercentage = (spacing * (float) i) + mXOffset;
                float pointOffset = pointOffsetPercentage * (float) (getSizeOfBuffer() - 1);
                float arrayPosRemainder = pointOffset % 1;

                if (arrayPosRemainder == 0) {
                    scaledBuffer[i] = mBuffer[(int) pointOffset];
                } else {
                    int lowerPosition = (int) Math.floor(pointOffset);
                    int upperPosition = (int) Math.ceil(pointOffset);

                    float lowerValue = mBuffer[lowerPosition];
                    float upperValue = mBuffer[upperPosition];

                    scaledBuffer[i] = lowerValue + ((upperValue - lowerValue) * arrayPosRemainder);
                }

            }
        }

        return scaledBuffer;
    }

    public int getId() {
        return mId;
    }

    public int getSizeOfBuffer() {
        return mBuffer.length;
    }

    public float getYOffset() {
        return mYOffset;
    }

    public float getXOffset() {
        return mXOffset;
    }

    public void setXOffset(float xOffset) {
        if (xOffset < 0f || xOffset > 1f) {
            Log.w(TAG, "display offset out of bounds 0-1");
        }

        // Ensure that the zoom level will be within the bounds of the screen
        if ((xOffset + mXScale) > 1f) {
            mXOffset = 1f - mXScale;
        } else {
            mXOffset = xOffset;
        }

    }

    public float getYScale() {
        return mYScale;
    }

    public float getXScale() {
        return mXScale;
    }

    public void setXScale(float xScale) {
        if (xScale < 0f || xScale > 1f) {
            Log.w(TAG, "zoom level out of bounds 0-1");
            return;
        }

        // Ensure that the zoom level will be within the bounds of the screen
        if ((xScale + mXOffset) > 1f) {
            mXOffset = 1f - xScale;
        }

        mXScale = xScale;
    }

    public SignalScale getSignalScale() {
        return mSignalScale;
    }

    enum SignalScale {
        logarithmic,
        linear
    }
}
