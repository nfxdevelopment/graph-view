package com.nfx.android.graph.androidgraph;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.nfx.android.graph.R;
import com.nfx.android.graph.androidgraph.markerList.MarkerAdapter;
import com.nfx.android.graph.androidgraph.markerList.MarkerModel;

import java.util.ArrayList;
import java.util.List;

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
    private GraphView mGraphView;
    /**
     * List of all the markers
     */
    private List<MarkerModel> mMarkerList;
    /**
     * Object to control the list view of marker information
     */
    private MarkerAdapter mMarkerAdapter;

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
        mGraphView = new GraphView(getContext(), attr);
        initialise();
    }

    /**
     * Called from the constructors for a generic setup
     */
    private void initialiseWithoutAttributes() {
        mGraphView = new GraphView(getContext());
        initialise();
    }

    /**
     * Inflate and setup all the views within the layout
     */
    private void initialise() {
        inflate(getContext(), R.layout.nfx_graph, this);

        if(mGraphView.getParent() != null) {
            ((ViewGroup) mGraphView.getParent()).removeView(mGraphView);
        }
        FrameLayout graphView = (FrameLayout)findViewById(R.id.graph);
        graphView.addView(mGraphView);
        ListView mMarkerInformation = (ListView) findViewById(R.id.marker_information);

        mMarkerList = new ArrayList<>();
        mMarkerAdapter = new MarkerAdapter(getContext(), mMarkerList);

        mMarkerInformation.setAdapter(mMarkerAdapter);

        mGraphView.getBackgroundManager().setBackgroundColour(
                getContext().getColor(R.color.background));
        mGraphView.getBackgroundManager().setGridLineColour(
                getContext().getColor(R.color.gridLines));
    }

    /**
     * Sets the markers on or off on a specific signal
     *
     * @param signalId  signal to apply the markers to
     * @param isShown   are the markers shown on the signal
     */
    public void setMarkers(int signalId, boolean isShown) {
        if(isShown) {
            addMarker(signalId, getContext().getColor(R.color.marker1));
            addMarker(signalId, getContext().getColor(R.color.marker2));
        } else {
            removeMarker(signalId);
            mMarkerList.clear();
        }

        mMarkerAdapter.notifyDataSetChanged();
    }

    /**
     * Add a marker to a given signal
     *
     * @param signalId  signal id to apply markers to
     * @param colour    colour of marker
     */
    private void addMarker(int signalId, int colour) {
        MarkerModel markerModel = new MarkerModel(mMarkerAdapter,
                getGraphView().getGraphSignalInputInterface());
        mMarkerList.add(markerModel);
        getGraphView().getSignalManager().addMarker(colour, signalId, markerModel);
    }

    /**
     * Remove markers on given signal
     * @param signalId a signal id
     */
    private void removeMarker(int signalId) {
        getGraphView().getSignalManager().removeMarkers(signalId);
    }

    /**
     * @return graph view from the manager
     */
    public GraphView getGraphView() {
        return mGraphView;
    }

}
