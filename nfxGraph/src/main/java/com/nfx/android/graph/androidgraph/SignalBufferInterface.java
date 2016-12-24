package com.nfx.android.graph.androidgraph;

import android.support.annotation.Nullable;

import com.nfx.android.graph.androidgraph.AxisScale.AxisParameters;
import com.nfx.android.graph.graphbufferinput.InputListener;

/**
 * NFX Development
 * Created by nick on 17/07/16.
 * <p/>
 * An interface to communicate to the input or the Signal Buffer to display
 */
class SignalBufferInterface extends InputListener {
    @Nullable
    private SignalBuffer signalBuffer = null;

    /**
     * Constructor
     *
     * @param signalBuffer the display object
     */
    SignalBufferInterface(@Nullable SignalBuffer signalBuffer) {
        this.signalBuffer = signalBuffer;
    }

    /**
     * Get the latest buffer information from signal. Minimum and maximum values for a given
     * position are available. If there is only one value at the position minimum and maximum are
     * equal
     *
     * @param minimumValuesBuffer representation of the minimum values of the signal
     * @param maximumValuesBuffer representation of the maximum values of the signal
     * @param maximumXValue return buffer from this start X position
     * @param minimumXValue return buffer to this end X position
     * @param xAxisParameters x axis parameters
     * */
    void getScaledMinimumMaximumBuffers(float[] minimumValuesBuffer, float[] maximumValuesBuffer,
                                        float minimumXValue, float maximumXValue,
                                        AxisParameters xAxisParameters) {
        if(signalBuffer != null) {
            signalBuffer.getScaledMinimumMaximumBuffers(minimumValuesBuffer, maximumValuesBuffer,
                    minimumXValue, maximumXValue, xAxisParameters);
        }
    }

    /**
     * Get a value from the buffer
     *
     * @param position a value between minimumX and maximumX
     */
    float getValueAtPosition(float position) {
        if(signalBuffer != null) {
            return signalBuffer.getValueAtPosition(position);
        } else {
            return 0;
        }
    }

    @Override
    public void inputBlockSizeUpdate(int blockSize) {
        if(signalBuffer != null) {
            signalBuffer.setSizeOfBuffer(blockSize);
        }
    }

    /**
     * Update the buffer
     *
     * @param buffer new data in which to set
     */
    public void bufferUpdate(float[] buffer) {
        if(signalBuffer != null) {
            signalBuffer.setBuffer(buffer);
        }
    }

    @Override
    public void inputRemoved() {
        // TODO Look at a way to automate the removal of a signal
    }
}
