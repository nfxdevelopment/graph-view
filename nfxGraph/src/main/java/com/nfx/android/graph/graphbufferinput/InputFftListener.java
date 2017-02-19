package com.nfx.android.graph.graphbufferinput;

/**
 * NFX Development
 * Created by nick on 16/02/17.
 */
public interface InputFftListener {
    void fftBufferUpdate(double[] magnitudes, double[] phase);
}
