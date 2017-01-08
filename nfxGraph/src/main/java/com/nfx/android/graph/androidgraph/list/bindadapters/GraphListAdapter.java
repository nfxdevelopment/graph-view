package com.nfx.android.graph.androidgraph.list.bindadapters;

import com.nfx.android.graph.androidgraph.list.binders.AverageFrequencyBinder;
import com.nfx.android.graph.androidgraph.list.binders.MarkerBinder;
import com.nfx.android.graph.androidgraph.list.data.AverageFrequencyData;
import com.nfx.android.graph.androidgraph.list.data.MarkerData;
import com.yqritc.recyclerviewmultipleviewtypesadapter.ListBindAdapter;

import java.util.List;

/**
 * NFX Development
 * Created by nick on 7/01/17.
 */
public class GraphListAdapter extends ListBindAdapter {

    public GraphListAdapter() {
        addBinder(new MarkerBinder(this));
        addBinder(new AverageFrequencyBinder(this));
    }

    public void setMarkerList(List<MarkerData> dataSet) {
        ((MarkerBinder) getDataBinder(0)).addAll(dataSet);

    }

    public void removeMarkerList() {
        ((MarkerBinder) getDataBinder(0)).clear();

    }

    public void setAverageFrequencyList(List<AverageFrequencyData> dataSet) {
        ((AverageFrequencyBinder) getDataBinder(1)).addAll(dataSet);

    }

    public void removeAverageFrequencyList() {
        ((AverageFrequencyBinder) getDataBinder(1)).clear();

    }
}