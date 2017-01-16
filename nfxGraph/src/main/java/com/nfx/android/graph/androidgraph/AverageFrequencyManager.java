package com.nfx.android.graph.androidgraph;

import com.nfx.android.graph.androidgraph.list.bindadapters.GraphListAdapter;
import com.nfx.android.graph.androidgraph.list.data.AverageFrequencyData;
import com.nfx.android.graph.graphbufferinput.InputInterface;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * NFX Development
 * Created by nick on 7/01/17.
 * <p>
 * A Object to calculate the average frequency on a time plot
 */
class AverageFrequencyManager implements AverageFrequencyManagerInterface {
    /**
     * List of all the markers
     */
    private AbstractMap<Integer, AverageFrequencyData> averageFrequencyDataMap = new HashMap<>();


    /**
     * Object to control the list view of marker information
     */
    private GraphListAdapter graphListAdapter;

    AverageFrequencyManager(GraphListAdapter graphListAdapter) {
        this.graphListAdapter = graphListAdapter;
    }

    public void addAverageFrequencyListener(int signalId, int signalColor, InputInterface input) {
        AverageFrequencyData averageFrequencyData =
                new AverageFrequencyData(input.getSampleRate(), signalColor);

        input.addInputListener(averageFrequencyData);

        averageFrequencyDataMap.put(signalId, averageFrequencyData);

        List<AverageFrequencyData> averageFrequencyDataList = new ArrayList<>();
        averageFrequencyDataList.addAll(averageFrequencyDataMap.values());

        graphListAdapter.setAverageFrequencyList(averageFrequencyDataList);
    }

    public boolean hasAverageFrequencyListener(int signalId) {
        return averageFrequencyDataMap.containsKey(signalId);
    }

    public void removeAverageFrequencyListener(int signalId) {
        averageFrequencyDataMap.remove(signalId);
        List<AverageFrequencyData> averageFrequencyDataList = new ArrayList<>();
        averageFrequencyDataList.addAll(averageFrequencyDataMap.values());

        graphListAdapter.setAverageFrequencyList(averageFrequencyDataList);
    }

    public void removeAll() {
        averageFrequencyDataMap.clear();
        graphListAdapter.removeAverageFrequencyList();
    }
}
