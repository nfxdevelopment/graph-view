package com.nfx.android.graph.androidgraph;

import android.content.Context;
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
        initialiseWithoutAttributes();
    }

    /**
     * @param context application context
     * @param attrs   attributes to be applied
     */
    public GraphManager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialiseWithAttributes(attrs);
    }

    /**
     * @param context application context
     * @param attrs attributes to be applied
     * @param defStyleAttr style attributes to be applied
     */
    public GraphManager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialiseWithAttributes(attrs);
    }

    /**
     * Called from the constructors for a generic setup with attributes to be applied
     * @param attr attributes to be applied
     */
    private void initialiseWithAttributes(AttributeSet attr) {
        graphView = new GraphView(getContext(), attr);
        initialise();
    }

    /**
     * Called from the constructors for a generic setup
     */
    private void initialiseWithoutAttributes() {
        graphView = new GraphView(getContext());
        initialise();
    }

    /**
     * Inflate and setup all the views within the layout
     */
    private void initialise() {
        inflate(getContext(), R.layout.nfx_graph, this);

        if(graphView.getParent() != null) {
            ((ViewGroup) graphView.getParent()).removeView(graphView);
        }
        FrameLayout graphView = (FrameLayout)findViewById(R.id.graph);
        graphView.addView(this.graphView);

        this.graphView.getBackgroundManager().setBackgroundColour(
                ContextCompat.getColor(getContext(), R.color.background));
        this.graphView.getBackgroundManager().setGridLineColour(
                ContextCompat.getColor(getContext(), R.color.gridLines));

        graphListManager = new GraphListManager(getContext(), getGraphView(), this);
        graphListManager.initialise();
    }


    /**
     * @return graph view from the manager
     */
    public GraphView getGraphView() {
        return graphView;
    }

    /**
     * @return marker manager
     */
    public MarkerManager getMarkerManager() {
        return graphListManager.getMarkerManager();
    }

    /**
     * @return average frequency manager
     */
    public AverageFrequencyManager getAverageFrequencyManager() {
        return graphListManager.getAverageFrequencyManager();
    }

}
