package com.nfx.android.library.androidgraph;

import android.util.Log;

/**
 * NFX Development
 * Created by nick on 31/10/15.
 * <p/>
 * A signal buffer holds a buffer with additional information on how it should be displayed on
 * screen. This can be used to pass buffer information between graphical and input objects.
 */
public abstract class SignalBuffer {
    private static final String TAG = "SignalBuffer";
    protected final float mAxisSpanValue;
    /**
     * Information about the scaling of signal in the y axis
     */
    final ZoomDisplay mYZoomDisplay;
    /**
     * Information about the scaling of signal in the y axis
     */
    final ZoomDisplay mXZoomDisplay;
    /**
     * buffer of given size which is worked out at runtime. This data is normalized 0-1
     */
    final float[] mBuffer;
    /**
     * Constructor
     *
     * @param sizeOfBuffer size expecting to receive
     */
    @SuppressWarnings("WeakerAccess")
    public SignalBuffer(int sizeOfBuffer, float axisSpanValue) {
        mBuffer = new float[sizeOfBuffer];
        mAxisSpanValue = axisSpanValue;

        mXZoomDisplay = new ZoomDisplay(1f, 0f);
        mYZoomDisplay = new ZoomDisplay(1f, 0f);
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
                Log.w(TAG, "Buffer passed " + buffer.length +
                        " in does not match size of signal buffer " + mBuffer.length);
            }
        }
    }
    /**
     * This will return a buffer with the desired {@code numberOfPoints} size. Essentially scaling
     * scaling the read buffer
     * The algorithm works out each new point separately. More often than not the new sample will
     * fall between to of buffer points therefore we have to do some aliasing
     *
     * @param scaledBuffer buffer to fill
     */
    public abstract void getScaledBuffer(float[] scaledBuffer);

    public int getSizeOfBuffer() {
        return mBuffer.length;
    }

    public float getAxisSpanValue() {
        return mAxisSpanValue;
    }

    public synchronized ZoomDisplay getXZoomDisplay() {
        return mXZoomDisplay;
    }

    public synchronized ZoomDisplay getYZoomDisplay() {
        return mYZoomDisplay;
    }
}
