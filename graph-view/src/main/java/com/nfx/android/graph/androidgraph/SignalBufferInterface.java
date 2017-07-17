package com.nfx.android.graph.androidgraph;

import com.nfx.android.graph.androidgraph.AxisScale.AxisParameters;

/**
 * NFX Development
 * Created by nick on 17/07/16.
 * <p>
 * An interface to communicate to the input or the Signal Buffer to display
 */
public interface SignalBufferInterface {

    void getScaledMinimumMaximumBuffers(float[] minimumValuesBuffer, float[] maximumValuesBuffer,
                                        float minimumXValue, float maximumXValue,
                                        AxisParameters xAxisParameters);

    float[] getUnscaledBuffer();

    float getValueAtPosition(float position);

    void inputBlockSizeUpdate(int blockSize);

    void bufferUpdate(float[] buffer);

    void inputRemoved();

    ZoomDisplay getYZoomDisplay();

    AxisParameters getXAxisParameters();
}
