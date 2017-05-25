package com.nfx.android.graph.androidgraph;

import android.graphics.Canvas;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;

import com.nfx.android.graph.androidgraph.AxisScale.AxisParameters;
import com.nfx.android.graph.graphbufferinput.InputListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NFX Development
 * Created by nick on 31/10/15.
 * <p/>
 * This is the middle man between the graph drawer and the input. All inputs are registered with
 * this manager and it will ask to create a drawer objects for these
 */
class SignalManager implements SignalManagerInterface {
    private static final String TAG = SignalManager.class.getName();
    /**
     * parent object
     */
    private final GraphViewInterface graphViewInterface;
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
     * Marker integration
     */
    private MarkerManager markerManager;
    /**
     * Label point to denote 0 seconds
     */
    @Nullable
    private LabelPointer xAxisZeroIntersect;
    /**
     * Current drawable area
     */
    private DrawableArea drawableArea = new DrawableArea(0, 0, 0, 0);
    /**
     * Constructor
     *
     * @param graphViewInterface needed to set the axis zoom levels
     */
    SignalManager(GraphViewInterface graphViewInterface, GraphListManager graphListManager) {
        this.graphViewInterface = graphViewInterface;

        markerManager = new MarkerManager(graphViewInterface, this,
                graphListManager.getGraphListAdapter());
    }

    /**
     * Use to add another signal into the collection. If the Id is not unique it will remove the
     * signal with the given id and display a warning
     *  @param sizeOfBuffer size of the buffer to create
     * @param xAxisParameters scale of buffer x axis
     * @param colour colour of signal
     */
    @Override
    public InputListener addSignal(int id, int sizeOfBuffer, AxisParameters xAxisParameters,
                                   int colour) {
        ZoomDisplay zoomDisplay = new ZoomDisplayWithOffsetBounds(1f, 0f);
        if(graphViewInterface.getGraphYZoomDisplay() instanceof ZoomDisplayWithOffsetBounds) {
            zoomDisplay = new ZoomDisplayWithOffsetBounds(1f, 0f);
        } else if(graphViewInterface.getGraphYZoomDisplay() != null) {
            zoomDisplay = new ZoomDisplay(1f, 0f);
        }

        SignalBuffer signalBuffer = new SignalBuffer(sizeOfBuffer, xAxisParameters, zoomDisplay);
        addSignal(id, signalBuffer, colour);
        return signalBuffer;
    }

    @Override
    public void addSignal(int id, SignalBuffer signalBuffer, int colour) {
        synchronized(this) {
            if(signalBuffers.put(id, signalBuffer) != null) {
                Log.w(TAG, "signal id exists, overwriting");
            }
        }

        Signal signal = new Signal(graphViewInterface.getGraphParameters(),
                signalBuffer, graphViewInterface.getGraphXZoomDisplay());
        signal.surfaceChanged(drawableArea);
        signal.setColour(colour);

        synchronized(this) {
            signalDrawers.put(id, signal);
            markerManager.updateMarkers(id);
        }
    }


    @Override
    public boolean hasSignal(int id) {
        return signalBuffers.containsKey(id);
    }

    @Override
    @Nullable
    public HorizontalLabelPointer enableTriggerLevelPointer(int signalId, @ColorInt int colour) {
        Signal signal = signalDrawers.valueAt(signalId);
        if(signal != null) {
            return signal.enableTriggerLevelPointer(colour);
        } else {
            return null;
        }
    }

    @Override
    public HorizontalLabelPointer getTriggerLevelPointer(int signalId) {
        Signal signal = signalDrawers.valueAt(signalId);
        if(signal != null) {
            return signal.getTriggerLevelPointer();
        } else {
            return null;
        }
    }

    /**
     * Remove signal with given id from collection
     *
     * @param id id of the signal to remove
     */
    @Override
    public void removeSignal(int id) {
        synchronized(this) {
            signalBuffers.remove(id);
            signalDrawers.remove(id);
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
        final int signalDrawerSize = signalDrawers.size();
        for(int i = 0; i < signalDrawerSize; i++) {
            int key = signalDrawers.keyAt(i);
            signalDrawers.get(key).surfaceChanged(drawableArea);
        }
        if(xAxisZeroIntersect != null) {
            xAxisZeroIntersect.surfaceChanged(drawableArea);
        }
        markerManager.surfaceChanged(drawableArea);
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
            if(xAxisZeroIntersect != null) {
                xAxisZeroIntersect.doDraw(canvas);
            }
            markerManager.doDraw(canvas);
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
    private Map<Integer, SignalBuffer> getSignalBuffers() {
        return signalBuffers;
    }

    /**
     * Displays the zero crossing point on a signal graph. Note this is only applicable on a time vs
     * amplitude plot
     */
    public LabelPointer enableXAxisZeroIntersect(int colour) {
        xAxisZeroIntersect = new VerticalLabelPointer(
                graphViewInterface.getGraphXZoomDisplay());
        xAxisZeroIntersect.surfaceChanged(drawableArea);
        xAxisZeroIntersect.setColour(colour);

        return xAxisZeroIntersect;
    }

    public void enableYAxisIntercept(int signalId) {
        Signal signal = signalDrawers.valueAt(signalId);
        if(signal != null) {
            signal.enableYAxisZeroIntercept(signal.getColour());
        }
    }

    public void disableYAxisIntercept(int signalId) {
        Signal signal = signalDrawers.valueAt(signalId);
        if(signal != null) {
            signal.disableYAxisZeroIntercept();
        }
    }

    @Override
    public SignalBufferInterface getSignalBufferInterface(int signalId) {
        return getSignalBuffers().get(signalId);
    }

    @Override
    public MarkerManagerInterface getMarkerManagerInterface() {
        return markerManager;
    }

    @Nullable
    @Override
    public SignalBuffer signalWithinCatchmentArea(float positionY) {
        SignalBuffer signalBufferToReturn = null;
        if(signalBuffers.size() == 1) {
            signalBufferToReturn = (SignalBuffer) signalBuffers.values().toArray()[0];
        } else {
            // Ensure that the first zoom display is taken by setting a default point that is
            // completely off screen
            float nearestCentrePoint = 1000000;
            for(SignalBuffer signalBuffer : signalBuffers.values()) {
                float signalCentrePoint = 0.5f;
                signalCentrePoint -= signalBuffer.getYZoomDisplay().getDisplayOffsetPercentage();
                signalCentrePoint /= signalBuffer.getYZoomDisplay().getZoomLevelPercentage();

                float touchSignalCentreDifference = Math.abs(positionY - signalCentrePoint);

                if(touchSignalCentreDifference < nearestCentrePoint) {
                    nearestCentrePoint = touchSignalCentreDifference;
                    signalBufferToReturn = signalBuffer;
                }
            }
        }
        return signalBufferToReturn;
    }

    /**
     * Removes the zero crossing point on a signal graph.
     */
    public void disableXAxisZeroIntersect() {
        xAxisZeroIntersect = null;
    }

    public SparseArray<Signal> getSignalDrawers() {
        return signalDrawers;
    }

    /**
     * @return the manager for marker drawing
     */
    public MarkerManagerInterface getMarkerManager() {
        return markerManager;
    }
}