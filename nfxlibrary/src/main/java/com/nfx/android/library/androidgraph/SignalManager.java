package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.util.Log;

import com.nfx.android.library.androidgraph.AxisScale.AxisParameters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NFX Development
 * Created by nick on 31/10/15.
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
    private final Map<SignalBufferInterface, Signal> mSignalDrawers = new HashMap<>();
    /**
     * An object holding the signals to display
     */

    private final Map<SignalBufferInterface, SignalBuffer> mSignalBuffers = new
            ConcurrentHashMap<>();
    /**
     * Handles the drawing of a unlimited amount of Markers
     **/
    private List<Marker> mMarkers = new Vector<>();
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
     *
     * @param sizeOfBuffer size of the buffer to create
     */
    public SignalBufferInterface addSignalBuffer(int sizeOfBuffer, AxisParameters axisParameters) {
        SignalBuffer signalBuffer = new SignalBuffer(sizeOfBuffer, axisParameters);
        SignalBufferInterface signalBufferInterface = new SignalBufferInterface(signalBuffer);

        if(mSignalBuffers.put(signalBufferInterface, signalBuffer) != null) {
            Log.w(TAG, "signal id exists, overwriting");
        }

        Signal signal = new Signal(mGraphView.getGraphParameters(),
                signalBufferInterface, mGraphView.getXZoomDisplay());
        signal.surfaceChanged(mDrawableArea);
        mSignalDrawers.put(signalBufferInterface, signal);

        return signalBufferInterface;
    }

    void addMarker(int colour, SignalBufferInterface signalBufferInterface,
                   Marker.MarkerUpdateInterface markerUpdateInterface) {
        Marker marker = new Marker(mGraphView.getGraphSignalInputInterface(),
                signalBufferInterface, markerUpdateInterface);

        marker.surfaceChanged(mDrawableArea);
        marker.setColour(colour);

        mMarkers.add(marker);
    }

    /**
     * Remove signal with given id from collection
     *
     * @param signalBufferInterface unique object of the signal
     */
    public void removedSignalBuffer(SignalBufferInterface signalBufferInterface) {
        mSignalBuffers.remove(signalBufferInterface);
        mSignalDrawers.remove(signalBufferInterface);
        for(int i = 0; i < mMarkers.size(); i++) {
            if(signalBufferInterface.equals(mMarkers.get(i).getSignal())) {
                mMarkers.remove(i);
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
        mDrawableArea = drawableArea;
        for(Signal signal : mSignalDrawers.values()) {
            signal.surfaceChanged(drawableArea);
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
        for(Signal signal : mSignalDrawers.values()) {
            signal.doDraw(canvas);
        }
        for(Marker marker : mMarkers) {
            marker.doDraw(canvas);
        }
    }

    /**
     * Remove signal drawers when stopped
     */
    public void removeSignalDrawers() {
        mSignalDrawers.clear();
    }

    public Map<SignalBufferInterface, Signal> getSignalDrawers() {
        return mSignalDrawers;
    }

    public Map<SignalBufferInterface, SignalBuffer> getSignalBuffers() {
        return mSignalBuffers;
    }
}