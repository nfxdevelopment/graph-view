package com.nfx.android.graph.androidgraph;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ListView;

import com.nfx.android.graph.R;
import com.nfx.android.graph.androidgraph.markerList.MarkerAdapter;
import com.nfx.android.graph.androidgraph.markerList.MarkerModel;

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
     * Graph Manager view used to find ID's
     */
    private final View parentView;
    /**
     * Graph view where markers will be displayed
     */
    private final GraphView graphView;
    /**
     * List of all the markers
     */
    private List<MarkerModel> markerList;
    /**
     * Object to control the list view of marker information
     */
    private MarkerAdapter markerAdapter;

    /**
     * @param context    context where the markers will be displayed
     * @param parentView parent view of markers
     */
    MarkerManager(Context context, GraphView graphView, View parentView) {
        this.context = context;
        this.parentView = parentView;
        this.graphView = graphView;
    }

    public void initialise() {
        ListView mMarkerInformation = (ListView) parentView.findViewById(R.id.marker_information);

        markerList = new ArrayList<>();
        markerAdapter = new MarkerAdapter(context, markerList);

        mMarkerInformation.setAdapter(markerAdapter);
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
        } else {
            removeMarker(signalId);
            markerList.clear();
        }

        markerAdapter.notifyDataSetChanged();
    }

    /**
     * Represents the X number as a floating point
     */
    public void representXAsFloat() {
        markerAdapter.setXIsInteger(false);
    }

    /**
     * Represents the X number as a floating point
     */
    public void representYAsFloat() {
        markerAdapter.setYIsInteger(false);
    }

    /**
     * Represents the X number as a integer
     */
    public void representXAsInteger() {
        markerAdapter.setXIsInteger(true);
    }

    /**
     * Represents the X number as a integer
     */
    public void representYAsInteger() {
        markerAdapter.setYIsInteger(true);
    }

    /**
     * Add a marker to a given signal
     *
     * @param signalId signal id to apply markers to
     * @param colour   colour of marker
     */
    private void addMarker(int signalId, int colour) {
        MarkerModel markerModel = new MarkerModel(markerAdapter,
                graphView.getGraphSignalInputInterface(),
                graphView.getSignalManager().getSignalBuffers().get(signalId));
        markerList.add(markerModel);
        graphView.getSignalManager().addMarker(colour, signalId, markerModel);
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
