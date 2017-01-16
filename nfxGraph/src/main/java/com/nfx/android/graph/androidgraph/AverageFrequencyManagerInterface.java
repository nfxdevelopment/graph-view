package com.nfx.android.graph.androidgraph;

import com.nfx.android.graph.graphbufferinput.InputInterface;

/**
 * NFX Development
 * Created by nick on 15/01/17.
 */
public interface AverageFrequencyManagerInterface {
    void addAverageFrequencyListener(int signalId, int signalColor, InputInterface input);

    boolean hasAverageFrequencyListener(int signalId);

    void removeAverageFrequencyListener(int signalId);

    void removeAll();
}
