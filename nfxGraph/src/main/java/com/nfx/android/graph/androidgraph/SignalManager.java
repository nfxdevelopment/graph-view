package com.nfx.android.graph.androidgraph;

import android.graphics.Canvas;
import android.support.annotation.Nullable;
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
    private final GraphView graphView;

    /**
     * Array of drawers to display signals
     */
    private final SparseArray<Signal> signalDrawers = new SparseArray<>();
    /**
     * An object holding the signals to display
     */
    private final Map<Integer, SignalBuffer> signalBuffers = new
            ConcurrentHashMap<>();
    /**
     * Handles the drawing of a unlimited amount of Markers
     **/
    private final List<Marker> markers = new Vector<>();
    /**
     * Label point to denote 0 seconds
     */
    @Nullable
    private LabelPointer xAxisZeroIntersect;
    /**
     * Show a pointer and line to show the yAxis Zero Intercept
     */
    private boolean showYAxisIntercept = false;
    /**
     * Current drawable area
     */
    private DrawableArea drawableArea = new DrawableArea(0, 0, 0, 0);
    /**
     * Constructor
     *
     * @param graphView needed to set the axis zoom levels
     */
    SignalManager(GraphView graphView) {
        this.graphView = graphView;
    }

    public List<Marker> getMarkers() {
        return markers;
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
            if(signalBuffers.put(id, signalBuffer) != null) {
                Log.w(TAG, "signal id exists, overwriting");
            }
        }

        Signal signal = new Signal(graphView.getGraphParameters(),
                signalBufferInterface, graphView.getXZoomDisplay());
        signal.surfaceChanged(drawableArea);
        signal.setColour(color);
        if(showYAxisIntercept) {
            signal.enableYAxisZeroIntercept(color);
        } else {
            signal.disableYAxisZeroIntercept();
        }

        synchronized(this) {
            signalDrawers.put(id, signal);
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
        Marker marker = new Marker(id, graphView.getGraphSignalInputInterface(),
                signalDrawers.get(id).getSignalBufferInterface(),
                markerUpdateInterface);

        marker.surfaceChanged(drawableArea);
        marker.setColour(colour);

        markers.add(marker);
    }

    /**
     * Update the markers with signal Id to look at the correct signal buffer
     *
     * @param signalId signal id
     */
    private void updateMarkers(int signalId) {
        for(Marker marker : markers) {
            if(marker.getSignalId() == signalId) {
                marker.setSignalInterface(signalDrawers.get(signalId).getSignalBufferInterface());
            }
        }
    }

    /**
     * Remove all markers attached to a specific signal
     *
     * @param signalId signal id
     */
    void removeMarkers(int signalId) {
        for(Iterator<Marker> iterator = markers.iterator(); iterator.hasNext(); ) {
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
            signalBuffers.remove(id);
            signalDrawers.remove(id);
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
        this.drawableArea = drawableArea;
        final int signalDrawerSize = signalDrawers.size();
        for(int i = 0; i < signalDrawerSize; i++) {
            int key = signalDrawers.keyAt(i);
            signalDrawers.get(key).surfaceChanged(drawableArea);
        }
        for(Marker marker : markers) {
            marker.surfaceChanged(drawableArea);
        }
        if(xAxisZeroIntersect != null) {
            xAxisZeroIntersect.surfaceChanged(drawableArea);
        }
    }

    /**
     * Call with the canvas to draw on
     *
     * @param canvas canvas to draw the objects onto
     */
    public void doDraw(Canvas canvas) {
        synchronized(this) {
            final int signalDrawerSize = signalDrawers.size();
            for(int i = 0; i < signalDrawerSize; i++) {
                int key = signalDrawers.keyAt(i);
                signalDrawers.get(key).doDraw(canvas);
            }
            for(Marker marker : markers) {
                marker.doDraw(canvas);
            }
            if(xAxisZeroIntersect != null) {
                xAxisZeroIntersect.doDraw(canvas);
            }
        }
    }

    /**
     * Remove signal drawers when stopped
     */
    @SuppressWarnings("unused")
    public void removeSignalDrawers() {
        synchronized(this) {
            signalDrawers.clear();
        }
    }

    /**
     * @return signal buffers
     */
    public Map<Integer, SignalBuffer> getSignalBuffers() {
        return signalBuffers;
    }

    /**
     * Displays the zero crossing point on a signal graph. Note this is only applicable on a time vs
     * amplitude plot
     */
    public void enableXAxisZeroIntersect(int colour) {
        xAxisZeroIntersect = new VerticalLabelPointer(
                graphView.getGraphSignalInputInterface().getGraphXZoomDisplay());
        xAxisZeroIntersect.surfaceChanged(drawableArea);
        xAxisZeroIntersect.setColour(colour);
    }

    /**
     * Removes the zero crossing point on a signal graph.
     */
    public void disableXAxisZeroIntersect() {
        xAxisZeroIntersect = null;
    }

    @Nullable
    public LabelPointer getXAxisZeroIntersect() {
        return xAxisZeroIntersect;
    }

    public void enableYAxisIntercept() {
        showYAxisIntercept = true;
        for(int i = 0; i < signalDrawers.size(); i++) {
            Signal signal = signalDrawers.valueAt(i);
            signal.enableYAxisZeroIntercept(signal.getColour());
        }
    }

    public void disableYAxisIntercept() {
        showYAxisIntercept = false;
        for(int i = 0; i < signalDrawers.size(); i++) {
            signalDrawers.valueAt(i).disableYAxisZeroIntercept();
        }
    }
}