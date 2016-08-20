package com.nfx.android.library.graphbufferinput;

import com.nfx.android.library.androidgraph.GraphView;

/**
 * NFX Development
 * Created by nick on 14/12/15.
 */
@SuppressWarnings("unused")
public class TestInput extends Input {

    public TestInput(GraphView.GraphSignalInputInterface graphSignalInputInterface) {
        super(graphSignalInputInterface);

        initialise();
    }

    @Override
    public void initialise() {
        int length = 8000;
        float[] buffer = new float[length];

        for(int i = 0; i < length; i++) {
            buffer[i] = (float) i / (float) length;
        }

        //noinspection ConstantConditions
        for(InputListener inputListener : mInputListeners.values()) {
            inputListener.bufferUpdate(buffer);
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {

    }
}
