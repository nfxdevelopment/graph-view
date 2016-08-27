package com.nfx.android.library.graphbufferinput;

import com.nfx.android.library.androidgraph.GraphView;

import java.util.HashMap;
import java.util.Map;

/**
 * NFX Development
 * Created by nick on 10/11/15.
 */
public abstract class Input {
    /**
     * The interface in which to send updates to
     */
    final GraphView.GraphSignalInputInterface mGraphSignalInputInterface;
    /**
     * Interface to update buffer data
     */
    final Map<Integer, InputListener> mInputListeners = new HashMap<>();
    /**
     * Used to pause the input
     */
    boolean mPaused = false;

    Input(GraphView.GraphSignalInputInterface graphSignalInputInterface) {
        mGraphSignalInputInterface = graphSignalInputInterface;
    }

    /**
     * Initialise anything that needs to be setup prior to start
     */
    @SuppressWarnings("unused")
    public abstract void initialise();

    /**
     * Start the listeners
     */
    public abstract void start();

    /**
     * Stop the listeners
     */
    public abstract void stop();

    /**
     * Add an object to listen the input
     *
     * @param inputListener listener object
     */
    public void addInputListener(InputListener inputListener) {
        mInputListeners.put(inputListener.hashCode(), inputListener);
    }

    /**
     * Remove a listening object
     *
     * @param inputListener listener object
     */
    public void removeInputListener(InputListener inputListener) {
        mInputListeners.remove(inputListener.hashCode());
    }

    /**
     * destroy the buffers and listeners getting ready to die
     */
    public void destroy() {
        for(InputListener inputListener : mInputListeners.values()) {
            inputListener.inputRemoved();
        }
    }

    public boolean getPaused() {
        return mPaused;
    }

    public void setPaused(boolean paused) {
        mPaused = paused;
    }
}
