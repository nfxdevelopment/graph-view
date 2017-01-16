package com.nfx.android.graph.androidgraph;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nfx.android.graph.R;
import com.nfx.android.graph.androidgraph.list.bindadapters.GraphListAdapter;

/**
 * NFX Development
 * Created by nick on 7/01/17.
 */
class GraphListManager {
    /**
     * how often to update the list
     */
    private static final int listUpdateInMs = 100;
    /**
     * Context for marker list
     */
    private final Context context;
    /**
     * Graph Manager view used to find ID's
     */
    private final View parentView;
    /**
     * Handler to control update of list information
     */
    private final Handler handler = new Handler();
    /**
     * Manager to handle the display of average frequency analysers
     */
    private AverageFrequencyManager averageFrequencyManager;

    /**
     * Adapter for recycler view
     */
    private GraphListAdapter graphListAdapter = new GraphListAdapter();

    /**
     * @param context    context which the list is in
     * @param parentView parent of this
     */
    GraphListManager(Context context, View parentView) {
        this.parentView = parentView;
        this.context = context;
        averageFrequencyManager = new AverageFrequencyManager(graphListAdapter);
    }

    /**
     * Sets up the link between the adapter and parent view
     */
    void initialise() {
        RecyclerView listInformation =
                (RecyclerView) parentView.findViewById(R.id.list_information);
        listInformation.setAdapter(graphListAdapter);
        listInformation.setLayoutManager(new LinearLayoutManager(context));

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                graphListAdapter.notifyDataSetChanged();
                handler.postDelayed(this, listUpdateInMs);
            }
        };

        handler.postDelayed(runnable, listUpdateInMs);
    }

    AverageFrequencyManager getAverageFrequencyManager() {
        return averageFrequencyManager;
    }

    GraphListAdapter getGraphListAdapter() {
        return graphListAdapter;
    }
}
