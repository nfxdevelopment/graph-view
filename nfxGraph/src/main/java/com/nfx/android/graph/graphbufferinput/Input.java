package com.nfx.android.graph.graphbufferinput;

import android.util.SparseArray;

/**
 * NFX Development
 * Created by nick on 10/11/15.
 * <p/>
 * This is the bare bones of an buffer input. Other objects can subscribe to buffer and settings
 * updates. Inherit from this class and implement the input type
 */
public abstract class Input {
    /**
     * Interface to update buffer data
     */
    private final SparseArray<InputListener> inputListeners = new SparseArray<>();
    /**
     * Used to pause the input
     */
    boolean paused = false;

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
     * Add a listening object
     *
     * @param inputListener listener object
     */
    public void addInputListener(InputListener inputListener) {
        inputListeners.put(inputListener.hashCode(), inputListener);
    }

    /**
     * Remove a listening object
     *
     * @param inputListener listener object
     */
    public void removeInputListener(InputListener inputListener) {
        inputListeners.remove(inputListener.hashCode());
    }

    /**
     * Call when block size is changed
     *
     * @param blockSize the new input block size
     */
    @SuppressWarnings("WeakerAccess")
    protected void notifyListenersOfInputBlockSizeChange(int blockSize) {
        final int listenersSize = inputListeners.size();
        for(int i = 0; i < listenersSize; i++) {
            int key = inputListeners.keyAt(i);
            inputListeners.get(key).inputBlockSizeUpdate(blockSize);
        }
    }

    /**
     * Call when there is a buffer update
     *
     * @param buffer the new buffer
     */
    protected void notifyListenersOfBufferChange(float[] buffer) {
        final int listenersSize = inputListeners.size();
        for(int i = 0; i < listenersSize; i++) {
            int key = inputListeners.keyAt(i);
            inputListeners.get(key).bufferUpdate(buffer);
        }
    }

    /**
     * destroy the buffers and listeners getting ready to die
     */
    public void destroy() {
        final int listenersSize = inputListeners.size();
        for(int i = 0; i < listenersSize; i++) {
            int key = inputListeners.keyAt(i);
            inputListeners.get(key).inputRemoved();
        }
    }

    /**
     * @return if the input is paused
     */
    public boolean getPaused() {
        return paused;
    }

    /**
     * Set the input to pause/start
     *
     * @param paused set true to pause
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public abstract int getBufferSize();
}
