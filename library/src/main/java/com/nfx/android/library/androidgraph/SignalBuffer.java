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
     * Unique Id for signal
     */
    private int mId;

    /**
     * buffer of given size which is worked out at runtime. This data is normalized 0-1
     */
    private float[] mBuffer;

    SignalBuffer(int id, int sizeOfBuffer) {
        mId = id;
        mBuffer = new float[sizeOfBuffer];
    }

    public float[] getBuffer() {
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

    public int getId() {
        return mId;
    }

    public int getSizeOfBuffer() {
        return mBuffer.length;
    }

    public float getYOffset() {
        return mYOffset;
    }

    public float getYScale() {
        return mYScale;
    }
}
