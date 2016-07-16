package com.nfx.android.library.graphbufferinput;

import com.nfx.android.library.androidgraph.GraphManager;
import com.nfx.android.library.androidgraph.SignalBuffers;

/**
 * NFX Development
 * Created by nick on 10/11/15.
 */
public abstract class Input {
    /**
     * Signal object to send to the graph
     */
    final SignalBuffers mSignalBuffers = new SignalBuffers();
    /**
     * The interface in which to send updates to
     */
    final GraphManager.GraphSignalInputInterface mGraphSignalInputInterface;
    boolean mPaused = false;

    Input(GraphManager.GraphSignalInputInterface graphSignalInputInterface) {
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
     * destroy the buffers and listeners getting ready to die
     */
    @SuppressWarnings({"EmptyMethod", "unused"})
    public abstract void destroy();

    public SignalBuffers getSignalBuffers() {
        return mSignalBuffers;
    }

    public boolean getPaused() {
        return mPaused;
    }

    public void setPaused(boolean paused) {
        mPaused = paused;
    }
}
