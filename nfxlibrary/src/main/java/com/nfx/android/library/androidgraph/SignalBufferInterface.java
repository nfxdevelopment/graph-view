package com.nfx.android.library.androidgraph;

/**
 * NFX Development
 * Created by nick on 17/07/16.
 * <p/>
 * This is the middle man to communicate to the input or the Signal Buffer to display
 */
public class SignalBufferInterface {
    private SignalBuffer mSignalBuffer = null;

    /**
     * Constructor
     *
     * @param signalBuffer the display object
     */
    public SignalBufferInterface(SignalBuffer signalBuffer) {
        this.mSignalBuffer = signalBuffer;
    }

    /**
     * Update where the display object is pointing to
     *
     * @param signalBuffer
     */
    public void setSignalBuffer(SignalBuffer signalBuffer) {
        this.mSignalBuffer = signalBuffer;
    }

    /**
     * Get the latest buffer
     *
     * @param scaledBuffer array to pass back data
     */
    public void getScaledBuffer(float[] scaledBuffer) {
        mSignalBuffer.getScaledBuffer(scaledBuffer);
    }

    /**
     * Update the buffer
     *
     * @param buffer new data in which to set
     */
    public void setBuffer(float[] buffer) {
        if(mSignalBuffer != null) {
            mSignalBuffer.setBuffer(buffer);
        }
    }
}
