package com.nfx.android.graph.androidgraph;

import android.graphics.Canvas;

import com.nfx.android.graph.androidgraph.list.bindadapters.GraphListAdapter;
import com.nfx.android.graph.androidgraph.list.data.MarkerData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * NFX Development
 * Created by nick on 21/12/16.
 * <p>
 * This object will handle all interactions with the markers. Currently there is only one
 * configuration for the markers but in the long run there will be more.
 */
class MarkerManager implements MarkerManagerInterface {

    /**
     * Graph view where markers will be displayed
     */
    private final GraphViewInterface graphViewInterface;
    /**
     * Used to get signal information
     */
    private final SignalManagerInterface signalManagerInterface;
    /**
     * Handles the drawing of a unlimited amount of Markers
     **/
    private final List<Marker> markers = new Vector<>();
    /**
     * List of all the markers
     */
    private List<MarkerData> markerList = new ArrayList<>();
    /**
     * Object to control the list view of marker information
     */
    private GraphListAdapter graphListAdapter;
    /**
     * Current drawable area
     */
    private DrawableArea drawableArea = new DrawableArea(0, 0, 0, 0);

    private boolean xIsInteger = false;
    private boolean yIsInteger = false;

    /**
     * @param signalManagerInterface         Holder of marker drawing
     * @param graphListAdapter  adapter for the recycler view which is displaying the marker data
     */
    MarkerManager(GraphViewInterface graphViewInterface,
                  SignalManagerInterface signalManagerInterface, GraphListAdapter
                          graphListAdapter) {
        this.graphViewInterface = graphViewInterface;
        this.signalManagerInterface = signalManagerInterface;
        this.graphListAdapter = graphListAdapter;
    }

    /**
     * Call with the canvas to draw on
     * @param canvas canvas to draw the objects onto
     */
    public void doDraw(Canvas canvas) {
        synchronized (markers) {
            for (Marker marker : markers) {
                marker.doDraw(canvas);
            }
        }
    }

    /**
     * Call when the surface view changes it's dimensions the objects have to called in the correct
     * order to ensure they take up the correct space
     *
     * @param drawableArea the available area to draw
     */
    public void surfaceChanged(DrawableArea drawableArea) {
        this.drawableArea = drawableArea;

        synchronized (markers) {
            for (Marker marker : markers) {
                marker.surfaceChanged(drawableArea);
            }
        }
    }

    /**
     * Represents the X number as a floating point
     */
    @Override
    public void representXAsFloat() {
        xIsInteger = false;
    }

    /**
     * Represents the X number as a floating point
     */
    @Override
    public void representYAsFloat() {
        yIsInteger = false;
    }

    /**
     * Represents the X number as a integer
     */
    @Override
    public void representXAsInteger() {
        xIsInteger = true;
    }

    /**
     * Represents the X number as a integer
     */
    @Override
    public void representYAsInteger() {
        yIsInteger = true;
    }

    @Override
    public Marker markerWithinCatchmentArea(float positionX, float catchmentArea) {

        synchronized (markers) {
            for (Marker marker : markers) {
                if (positionX < (marker.getMarkerPositionInPx() + catchmentArea) &&
                        positionX > (marker.getMarkerPositionInPx() - catchmentArea)) {
                    return marker;
                }
            }
        }

        return null;
    }

    /**
     * Add a marker to a given signal
     *
     * @param signalId signal id to apply markers to
     * @param colour   colour of marker
     */
    @Override
    public void addMarker(int signalId, int colour) {
        MarkerData markerData = new MarkerData(graphViewInterface,
                signalManagerInterface.getSignalBufferInterface(signalId));
        markerData.setXIsInteger(xIsInteger);
        markerData.setYIsInteger(yIsInteger);
        markerList.add(markerData);

        Marker marker = new Marker(signalId, graphViewInterface,
                signalManagerInterface.getSignalBufferInterface(signalId), markerData);

        marker.surfaceChanged(drawableArea);
        marker.setColour(colour);

        synchronized (markers) {
            markers.add(marker);
        }

        graphListAdapter.setMarkerList(markerList);
    }

    /**
     * Update the markers with signal Id to look at the correct signal buffer
     *
     * @param signalId signal id
     */
    @Override
    public void updateMarkers(int signalId) {
        synchronized (markers) {
            for (Marker marker : markers) {
                if (marker.getSignalId() == signalId) {
                    marker.setSignalInterface(signalManagerInterface.getSignalBufferInterface
                            (signalId));
                }
            }
        }
    }

    /**
     * Remove markers on given signal
     *
     * @param signalId a signal id
     */
    @Override
    public void removeMarkers(int signalId) {
        synchronized (markers) {
            for (Iterator<Marker> iterator = markers.iterator(); iterator.hasNext(); ) {
                Marker marker = iterator.next();
                if (marker.getSignalId() == signalId) {
                    iterator.remove();
                }
            }
        }
        markerList.clear();
        graphListAdapter.removeMarkerList();
    }
}
