package com.nfx.android.library.graphbufferinput;

import com.nfx.android.library.androidgraph.GraphManager;
import com.nfx.android.library.androidgraph.SignalBuffers;
import com.nfx.android.library.graphuserinput.TouchInput;

/**
 * NFX Development
 * Created by nick on 10/11/15.
 */
public abstract class Input implements TouchInput.TouchListener {
    /**
     * The interface in which to send updates to
     */
    protected GraphManager.GraphSignalInputInterface mGraphSignalInputInterface;
    /**
     * Signal object to send to the graph
     */
    protected SignalBuffers mSignalBuffers = new SignalBuffers();

    public Input(GraphManager.GraphSignalInputInterface graphSignalInputInterface) {
        mGraphSignalInputInterface = graphSignalInputInterface;
    }

    /**
     * Initialise anything that needs to be setup prior to start
     */
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
     * destory the buffers and listeners getting ready to die
     */
    public abstract void destroy();
}
