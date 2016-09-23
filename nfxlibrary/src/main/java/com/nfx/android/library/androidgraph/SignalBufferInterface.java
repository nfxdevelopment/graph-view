package com.nfx.android.library.androidgraph;

import com.nfx.android.library.androidgraph.AxisScale.AxisParameters;
import com.nfx.android.library.graphbufferinput.InputListener;

/**
 * NFX Development
 * Created by nick on 17/07/16.
 * <p/>
 * An interface to communicate to the input or the Signal Buffer to display
 */
class SignalBufferInterface extends InputListener {
    private SignalBuffer mSignalBuffer = null;

    /**
     * Constructor
     *
     * @param signalBuffer the display object
     */
    SignalBufferInterface(SignalBuffer signalBuffer) {
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
    void getScaledBuffer(float[] scaledBuffer, float minimumXValue, float maximumXValue,
                         AxisParameters scaleToParameters) {
        mSignalBuffer.getScaledBuffer(scaledBuffer, minimumXValue, maximumXValue,
                scaleToParameters);
    }

    /**
     * Get a value from the buffer
     *
     * @param position a value between minimumX and maximumX
     */
    float getValueAtPosition(float position) {
        return mSignalBuffer.getValueAtPosition(position);
    }

    @Override
    public void inputBlockSizeUpdate(int blockSize) {
        mSignalBuffer.setSizeOfBuffer(blockSize);
    }

    /**
     * Update the buffer
     *
     * @param buffer new data in which to set
     */
    public void bufferUpdate(float[] buffer) {
        if(mSignalBuffer != null) {
            mSignalBuffer.setBuffer(buffer);
        }
    }

    @Override
    public void inputRemoved() {
        // TODO Look at a way to automate the removal of a signal
    }
}
