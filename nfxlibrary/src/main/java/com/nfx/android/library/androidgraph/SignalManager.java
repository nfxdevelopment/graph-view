package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
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
    private final GraphManager mGraphManager;

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
     * Current drawable area
     */
    private DrawableArea mDrawableArea = new DrawableArea(0, 0, 0, 0);

    /**
     * Constructor
     *
     * @param graphManager needed to set the axis zoom levels
     */
    SignalManager(GraphManager graphManager) {
        mGraphManager = graphManager;
    }

    /**
     * Use to add another signal into the collection. If the Id is not unique it will remove the
     * signal with the given id and display a warning
     *
     * @param sizeOfBuffer size of the buffer to create
     * @param signalScale  either linear or logarithmic for use when displaying
     */
    public SignalBufferInterface addSignalBuffer(int sizeOfBuffer, float axisSpanValue,
                                                 GraphManager.Scale signalScale) {
        SignalBuffer signalBuffer;
        if(signalScale == GraphManager.Scale.linear) {
            signalBuffer = new LinSignalBuffer(sizeOfBuffer, axisSpanValue);
        } else if(signalScale == GraphManager.Scale.logarithmic) {
            signalBuffer = new LogSignalBuffer(sizeOfBuffer, axisSpanValue);
        } else {
            Log.e(TAG, "Signal Scale unknown");
            return null;
        }

        SignalBufferInterface signalBufferInterface = new SignalBufferInterface(signalBuffer);

        if(mSignalBuffers.put(signalBufferInterface, signalBuffer) != null) {
            Log.w(TAG, "signal id exists, overwriting");
        }

        Signal signal = new Signal(signalBufferInterface);
        signal.surfaceChanged(mDrawableArea);
        mSignalDrawers.put(signalBufferInterface, signal);

        mGraphManager.getBackgroundManager().setZoomDisplay(signalBuffer.getXZoomDisplay(),
                signalBuffer.getYZoomDisplay());

        return signalBufferInterface;
    }

    /**
     * Remove signal with given id from collection
     *
     * @param signalBufferInterface unique object of the signal
     */
    public void removedSignalBuffer(SignalBufferInterface signalBufferInterface) {
        mSignalBuffers.remove(signalBufferInterface);
        mSignalDrawers.remove(signalBufferInterface);
    }

    public void removeSignal(int id) {
        mSignalDrawers.remove(id);
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
    }

    /**
     * Remove signal drawers when stopped
     */
    public void removeSignalDrawers() {
        mSignalDrawers.clear();
    }

    public Map<SignalBufferInterface, SignalBuffer> getSignalBuffers() {
        return mSignalBuffers;
    }

    public void setXAxisLogarithmic() {
        for(Map.Entry<SignalBufferInterface, SignalBuffer> signalBuffer : mSignalBuffers.entrySet
                ()) {
            SignalBuffer logSignalBuffer = new LogSignalBuffer(signalBuffer.getValue()
                    .getSizeOfBuffer(),
                    signalBuffer.getValue().getAxisSpanValue());

            signalBuffer.getKey().setSignalBuffer(logSignalBuffer);
            signalBuffer.setValue(logSignalBuffer);

            mGraphManager.getBackgroundManager().setZoomDisplay(logSignalBuffer.getXZoomDisplay(),
                    logSignalBuffer.getYZoomDisplay());
        }
    }

    public void setXAxisLinear() {
        for(Map.Entry<SignalBufferInterface, SignalBuffer> signalBuffer : mSignalBuffers.entrySet
                ()) {
            SignalBuffer linSignalBuffer = new LinSignalBuffer(signalBuffer.getValue()
                    .getSizeOfBuffer(),
                    signalBuffer.getValue().getAxisSpanValue());

            signalBuffer.getKey().setSignalBuffer(linSignalBuffer);
            signalBuffer.setValue(linSignalBuffer);

            mGraphManager.getBackgroundManager().setZoomDisplay(linSignalBuffer.getXZoomDisplay(),
                    linSignalBuffer.getYZoomDisplay());
        }
    }
}