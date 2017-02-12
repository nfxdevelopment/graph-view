package com.nfx.android.graph.androidgraph;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.nfx.android.graph.R;

/**
 * NFX Development
 * Created by nick on 22/07/16.
 * <p/>
 * Managers the creation of the graph and it's views. At the top level there is a graph view and
 * markers. See layout file
 */
public class GraphManager extends RelativeLayout {
    /**
     * Object which delegates all the graph drawing
     */
    private GraphView graphView;

    /**
     * A manager to help display markers
     */
    private GraphListManager graphListManager;

    /**
     * @param context application context
     */
    public GraphManager(Context context) {
        super(context);
        initialise(null);
    }

    /**
     * @param context application context
     * @param attrs   attributes to be applied
     */
    public GraphManager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise(attrs);
    }

    /**
     * @param context application context
     * @param attrs attributes to be applied
     * @param defStyleAttr style attributes to be applied
     */
    public GraphManager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise(attrs);
    }

    /**
     * Inflate and setup all the views within the layout
     */
    private void initialise(@Nullable AttributeSet attr) {
        inflate(getContext(), R.layout.nfx_graph, this);

        graphListManager = new GraphListManager(getContext(), this);
        if(attr != null) {
            graphView = new GraphView(getContext(), attr, graphListManager);
        } else {
            graphView = new GraphView(getContext(), graphListManager);
        }

        if(graphView.getParent() != null) {
            ((ViewGroup) graphView.getParent()).removeView(graphView);
        }
        FrameLayout graphView = (FrameLayout)findViewById(R.id.graph);
        graphView.addView(this.graphView);

        this.graphView.getBackgroundManager().setGridLineColour(
                ContextCompat.getColor(getContext(), R.color.gridLines));

        graphListManager.initialise();
    }


    /**
     * @return graph view from the manager
     */
    public GraphViewInterface getGraphViewInterface() {
        return graphView;
    }

    /**
     * @return marker manager
     */
    public MarkerManagerInterface getMarkerManagerInterface() {
        return getSignalManagerInterface().getMarkerManagerInterface();
    }

    /**
     * @return average frequency manager
     */
    public AverageFrequencyManagerInterface getAverageFrequencyManager() {
        return graphListManager.getAverageFrequencyManager();
    }

    public SignalManagerInterface getSignalManagerInterface() {
        return graphView.getSignalManager();
    }

    GraphListManager getGraphListManager() {
        return graphListManager;
    }

    public BackgroundManagerInterface getBackgroundManagerInterface() {
        return graphView.getBackgroundManager();
    }
}
