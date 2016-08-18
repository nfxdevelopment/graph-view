package com.nfx.android.library.graphbufferinput;

import com.nfx.android.library.androidgraph.GraphView;
import com.nfx.android.library.androidgraph.SignalBufferInterface;

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
    SignalBufferInterface mSignalBufferInterface;
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
     * destroy the buffers and listeners getting ready to die
     */
    @SuppressWarnings({"EmptyMethod", "unused"})
    public abstract void destroy();

    public boolean getPaused() {
        return mPaused;
    }

    public void setPaused(boolean paused) {
        mPaused = paused;
    }
}
