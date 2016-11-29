package com.nfx.android.graph.androidgraph;

import android.graphics.Canvas;
import android.util.Log;
import android.util.SparseArray;

import com.nfx.android.graph.androidgraph.AxisScale.AxisParameters;
import com.nfx.android.graph.graphbufferinput.InputListener;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NFX Development
 * Created by nick on 31/10/15.
 * <p/>
 * This is the middle man between the graph drawer and the input. All inputs are registered with
 * this manager and it will ask to create a drawer objects for these
 */
public class SignalManager {
    private static final String TAG = SignalManager.class.getName();
    /**
     * parent object
     */
    private final GraphView mGraphView;

    /**
     * Array of drawers to display signals
     */
    private final SparseArray<Signal> mSignalDrawers = new SparseArray<>();
    /**
     * An object holding the signals to display
     */
    private final Map<Integer, SignalBuffer> mSignalBuffers = new
            ConcurrentHashMap<>();
    /**
     * Handles the drawing of a unlimited amount of Markers
     **/
    private final List<Marker> mMarkers = new Vector<>();
    /**
     * Current drawable area
     */
    private DrawableArea mDrawableArea = new DrawableArea(0, 0, 0, 0);
    /**
     * Constructor
     *
     * @param graphView needed to set the axis zoom levels
     */
    SignalManager(GraphView graphView) {
        mGraphView = graphView;
    }

    public List<Marker> getMarkers() {
        return mMarkers;
    }

    /**
     * Use to add another signal into the collection. If the Id is not unique it will remove the
     * signal with the given id and display a warning
     *  @param sizeOfBuffer size of the buffer to create
     * @param xAxisParameters scale of buffer x axis
     * @param color color of signal
     */
    InputListener addSignalBuffer(int id, int sizeOfBuffer, AxisParameters xAxisParameters,
                                  int color) {
        SignalBuffer signalBuffer = new SignalBuffer(sizeOfBuffer, xAxisParameters);
        return addSignalBuffer(id, signalBuffer, color);
    }

    @SuppressWarnings("WeakerAccess")
    public InputListener addSignalBuffer(int id, SignalBuffer signalBuffer, int color) {
        SignalBufferInterface signalBufferInterface = new SignalBufferInterface(signalBuffer);
        synchronized(this) {
            if(mSignalBuffers.put(id, signalBuffer) != null) {
                Log.w(TAG, "signal id exists, overwriting");
            }
        }

        Signal signal = new Signal(mGraphView.getGraphParameters(),
                signalBufferInterface, mGraphView.getXZoomDisplay());
        signal.surfaceChanged(mDrawableArea);
        signal.setColour(color);

        synchronized(this) {
            mSignalDrawers.put(id, signal);
            updateMarkers(id);
        }

        return signalBufferInterface;
    }

    /**
     * Add a marker to the graph on a specific signal
     *
     * @param colour                colour of the marker
     * @param id                    signal id for marker to track
     * @param markerUpdateInterface interface to the marker
     */
    void addMarker(int colour, int id, Marker.MarkerUpdateInterface markerUpdateInterface) {
        Marker marker = new Marker(id, mGraphView.getGraphSignalInputInterface(),
                mSignalDrawers.get(id).getSignalBufferInterface(),
                markerUpdateInterface);

        marker.surfaceChanged(mDrawableArea);
        marker.setColour(colour);

        mMarkers.add(marker);
    }

    /**
     * Update the markers with signal Id to look at the correct signal buffer
     *
     * @param signalId signal id
     */
    private void updateMarkers(int signalId) {
        for(Marker marker : mMarkers) {
            if(marker.getSignalId() == signalId) {
                marker.setSignalInterface(mSignalDrawers.get(signalId).getSignalBufferInterface());
            }
        }
    }

    /**
     * Remove all markers attached to a specific signal
     *
     * @param signalId signal id
     */
    void removeMarkers(int signalId) {
        for(Iterator<Marker> iterator = mMarkers.iterator(); iterator.hasNext(); ) {
            Marker marker = iterator.next();
            if(marker.getSignalId() == signalId) {
                iterator.remove();
            }
        }
    }

    /**
     * Remove signal with given id from collection
     *
     * @param id id of the signal to remove
     */
    void removedSignalBuffer(int id) {
        synchronized(this) {
            mSignalBuffers.remove(id);
            mSignalDrawers.remove(id);
        }
        removeMarkers(id);
    }

    /**
     * Call when the surface view changes it's dimensions the objects have to called in the correct
     * order to ensure they take up the correct space
     *
     * @param drawableArea the available area to draw
     */
    public void surfaceChanged(DrawableArea drawableArea) {
        mDrawableArea = drawableArea;
        final int signalDrawerSize = mSignalDrawers.size();
        for(int i = 0; i < signalDrawerSize; i++) {
            int key = mSignalDrawers.keyAt(i);
            mSignalDrawers.get(key).surfaceChanged(drawableArea);
        }
        for(Marker marker : mMarkers) {
            marker.surfaceChanged(drawableArea);
        }
    }

    /**
     * Call with the canvas to draw on
     *
     * @param canvas canvas to draw the objects onto
     */
    public void doDraw(Canvas canvas) {
        synchronized(this) {
            final int signalDrawerSize = mSignalDrawers.size();
            for(int i = 0; i < signalDrawerSize; i++) {
                int key = mSignalDrawers.keyAt(i);
                mSignalDrawers.get(key).doDraw(canvas);
            }
            for(Marker marker : mMarkers) {
                marker.doDraw(canvas);
            }
        }
    }

    /**
     * Remove signal drawers when stopped
     */
    @SuppressWarnings("unused")
    public void removeSignalDrawers() {
        synchronized(this) {
            mSignalDrawers.clear();
        }
    }

    /**
     * @return signal buffers
     */
    public Map<Integer, SignalBuffer> getSignalBuffers() {
        return mSignalBuffers;
    }
}