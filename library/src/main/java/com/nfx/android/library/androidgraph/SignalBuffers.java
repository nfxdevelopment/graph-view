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
     * So we now if the data is logarithmic or linear
     */
    private SignalScale mSignalScale;
    /**
     * A collection of signal buffers which is synchronized
     */
    private Map<Integer, SignalBuffer> mSignalBuffers = new ConcurrentHashMap<>();

    SignalBuffers(SignalScale signalScale) {
        mSignalScale = signalScale;
    }

    public void addSignalBuffer(int id, SignalBuffer signalBuffer) {
        if (mSignalBuffers.put(id, signalBuffer) != null) {
            Log.w(TAG, "signal id exists, overwriting");
        }
    }

    public void removedSignalBuffer(int id) {
        mSignalBuffers.remove(id);
    }

    public Map<Integer, SignalBuffer> getSignalBuffer() {
        return mSignalBuffers;
    }

    public SignalScale getSignalScale() {
        return mSignalScale;
    }

    enum SignalScale {
        logarithmic,
        linear
    }
}
