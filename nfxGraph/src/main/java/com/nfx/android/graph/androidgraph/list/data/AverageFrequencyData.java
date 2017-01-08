package com.nfx.android.graph.androidgraph.list.data;

import com.nfx.android.graph.dsp.AverageFrequency;
import com.nfx.android.graph.graphbufferinput.InputListener;

/**
 * NFX Development
 * Created by nick on 7/01/17.
 */
public class AverageFrequencyData extends InputListener {

    private AverageFrequency averageFrequencyCalculator;
    private int signalColor;

    public AverageFrequencyData(int sampleRate, int signalColor) {
        this.signalColor = signalColor;
        averageFrequencyCalculator = new AverageFrequency(sampleRate);
    }

    @Override
    public void inputBlockSizeUpdate(int blockSize) {
        // N/A in this instance
    }

    @Override
    public void bufferUpdate(float[] buffer) {
        averageFrequencyCalculator.analyseBuffer(buffer);
    }

    @Override
    public void inputRemoved() {

    }

    public int getSignalColor() {
        return signalColor;
    }

    public int getAverageFrequency() {
        return averageFrequencyCalculator.getFrequency();
    }
}
