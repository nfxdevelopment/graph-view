package com.nfx.android.graph.graphbufferinput;

import android.util.SparseArray;

/**
 * NFX Development
 * Created by nick on 10/11/15.
 * <p/>
 * This is the bare bones of an buffer input. Other objects can subscribe to buffer and settings
 * updates. Inherit from this class and implement the input type
 */
public abstract class Input implements InputInterface {
    /**
     * Interface to update buffer data
     */
    private final SparseArray<InputListener> inputListeners = new SparseArray<>();
    /**
     * Is the input running
     */
    boolean running = false;
    /**
     * Used to pause the input
     */
    boolean paused = false;

    /**
     * Add a listening object
     *
     * @param inputListener listener object
     */
    @Override
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
    @Override
    public boolean isPaused() {
        return paused;
    }

    /**
     * pause the input
     */
    @Override
    public void pause() {
        this.paused = true;
    }

    /**
     * unpause the input signal
     */
    @Override
    public void unpause() {
        this.paused = false;
    }

    /**
     * @return is the audio capture thread running
     */
    @Override
    public boolean isRunning() {
        return running;
    }

    /**
     * @return input listeners for use of transferring the signal
     */
    @Override
    public SparseArray<InputListener> getInputListeners() {
        return inputListeners;
    }

}
