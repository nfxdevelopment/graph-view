package com.nfx.android.library.androidgraph;

import android.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NFX Development
 * Created by nick on 31/10/15.
 * <p/>
 * This object holds a collection of SignalBuffer which has a unified xScale for additional
 * information on how to display the buffers on screen.
 */
public class SignalBuffers {
    private static final String TAG = "SignalBuffers";

    /**
     * A collection of signal buffers which is synchronized
     */
    private final Map<Integer, SignalBuffer> mSignalBuffers = new ConcurrentHashMap<>();

    /**
     * Use to add another signal into the collection. If the Id is not unique it will remove the
     * signal with the given id and display a warning
     *
     * @param id           a unique id for th signal
     * @param sizeOfBuffer size of the buffer to create
     * @param signalScale  either linear or logarithmic for use when displaying
     */
    public void addSignalBuffer(int id, int sizeOfBuffer, SignalBuffer.SignalScale signalScale) {
        SignalBuffer signalBuffer = new SignalBuffer(id, sizeOfBuffer, signalScale);
        if (mSignalBuffers.put(id, signalBuffer) != null) {
            Log.w(TAG, "signal id exists, overwriting");
        }
    }

    /**
     * Remove signal with given id from collection
     *
     * @param id unique id of the signal
     */
    public void removedSignalBuffer(int id) {
        mSignalBuffers.remove(id);
    }

    /**
     * Get a reference to the collection
     *
     * @return a Map of signals which are in the collection
     */
    public Map<Integer, SignalBuffer> getSignalBuffer() {
        return mSignalBuffers;
    }
}
