package com.nfx.android.graph.androidgraph;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.nfx.android.graph.R;
import com.nfx.android.graph.androidgraph.list.bindadapters.GraphListAdapter;
import com.nfx.android.graph.androidgraph.list.data.MarkerData;

import java.util.ArrayList;
import java.util.List;

/**
 * NFX Development
 * Created by nick on 21/12/16.
 * <p>
 * This object will handle all interactions with the markers. Currently there is only one
 * configuration for the markers but in the long run there will be more.
 */
public class MarkerManager {

    /**
     * Context for marker list
     */
    private final Context context;
    /**
     * Graph view where markers will be displayed
     */
    private final GraphView graphView;
    /**
     * List of all the markers
     */
    private List<MarkerData> markerList = new ArrayList<>();
    /**
     * Object to control the list view of marker information
     */
    private GraphListAdapter graphListAdapter;

    private boolean xIsInteger = false;
    private boolean yIsInteger = false;

    /**
     * @param context           context where the markers will be displayed
     * @param graphView         Holder of marker drawing
     * @param graphListAdapter  adapter for the recycler view which is displaying the marker data
     */
    MarkerManager(Context context, GraphView graphView, GraphListAdapter graphListAdapter) {
        this.context = context;
        this.graphView = graphView;
        this.graphListAdapter = graphListAdapter;
    }

    /**
     * Sets the markers on or off on a specific signal
     *
     * @param signalId signal to apply the markers to
     * @param isShown  are the markers shown on the signal
     */
    public void setMarkers(int signalId, boolean isShown) {
        if(isShown) {
            addMarker(signalId, ContextCompat.getColor(context, R.color.marker1));
            addMarker(signalId, ContextCompat.getColor(context, R.color.marker2));

            graphListAdapter.setMarkerList(markerList);
        } else {
            removeMarker(signalId);
            markerList.clear();
            graphListAdapter.removeMarkerList();
        }
    }

    /**
     * Represents the X number as a floating point
     */
    public void representXAsFloat() {
        xIsInteger = false;
    }

    /**
     * Represents the X number as a floating point
     */
    public void representYAsFloat() {
        yIsInteger = false;
    }

    /**
     * Represents the X number as a integer
     */
    public void representXAsInteger() {
        xIsInteger = true;
    }

    /**
     * Represents the X number as a integer
     */
    public void representYAsInteger() {
        yIsInteger = true;
    }

    /**
     * Add a marker to a given signal
     *
     * @param signalId signal id to apply markers to
     * @param colour   colour of marker
     */
    private void addMarker(int signalId, int colour) {
        MarkerData markerData = new MarkerData(graphView.getGraphSignalInputInterface(),
                graphView.getSignalManager().getSignalBuffers().get(signalId));
        markerData.setXIsInteger(xIsInteger);
        markerData.setYIsInteger(yIsInteger);
        markerList.add(markerData);
        graphView.getSignalManager().addMarker(colour, signalId, markerData);
    }

    /**
     * Remove markers on given signal
     *
     * @param signalId a signal id
     */
    private void removeMarker(int signalId) {
        graphView.getSignalManager().removeMarkers(signalId);
    }

}
