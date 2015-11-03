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
    private Map<Integer, SignalBuffer> mSignalBuffers = new ConcurrentHashMap<>();

    /**
     * Use to add anothe signal into the collection. If the Id is not unique it will remove the
     * signal with the given id and display a warning
     *
     * @param id           a unique id for th signal
     * @param signalBuffer signal adding to collection
     */
    public void addSignalBuffer(int id, SignalBuffer signalBuffer) {
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
