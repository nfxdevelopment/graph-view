package com.nfx.android.graph.graphbufferinput;

import android.support.annotation.Nullable;
import android.util.SparseArray;

/**
 * NFX Development
 * Created by nick on 13/01/17.
 */
public interface InputInterface {
    void initialise();

    void start();

    void stop();

    void destroy();

    void pause();

    void unpause();

    boolean isPaused();

    boolean isRunning();

    int getSampleRate();

    int getBufferSize();

    void addInputListener(InputListener inputListener);

    SparseArray<InputListener> getInputListeners();

    void removeInputListener(InputListener inputListener);

    boolean hasTriggerDetection();

    @Nullable
    TriggerDetection getTriggerDetection();

}
