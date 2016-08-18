package com.nfx.android.library.androidgraph;

import com.nfx.android.library.androidgraph.AxisScale.AxisParameters;

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
     * @param signalBuffer set the buffer listen to
     */
    public void setSignalBuffer(SignalBuffer signalBuffer) {
        this.mSignalBuffer = signalBuffer;
    }

    /**
     * Get the latest buffer
     *
     * @param scaledBuffer array to pass back data
     * @param maximumXValue minimum value of scaled buffer
     * @param minimumXValue maximum value of scaled buffer
     * @param scaleToParameters target axis
     * */
    public void getScaledBuffer(float[] scaledBuffer, float minimumXValue, float maximumXValue,
                                AxisParameters scaleToParameters) {
        mSignalBuffer.getScaledBuffer(scaledBuffer, minimumXValue, maximumXValue,
                scaleToParameters);
    }

    /**
     * Get a value from the buffer
     *
     * @param position a value between minimumX and maximumX
     */
    public float getValueAtPosition(float position) {
        return mSignalBuffer.getValueAtPosition(position);
    }


    /**
     * return YAxis Zoom Display
     *
     * @return y axis Zoom display
     */
    public ZoomDisplay getYZoomDisplay() {
        return mSignalBuffer.getYZoomDisplay();
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
