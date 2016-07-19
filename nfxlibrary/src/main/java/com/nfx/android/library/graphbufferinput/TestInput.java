package com.nfx.android.library.graphbufferinput;

import com.nfx.android.library.androidgraph.GraphManager;

/**
 * NFX Development
 * Created by nick on 14/12/15.
 */
@SuppressWarnings("unused")
public class TestInput extends Input {

    public TestInput(GraphManager.GraphSignalInputInterface graphSignalInputInterface) {
        super(graphSignalInputInterface);

        initialise();
    }

    @Override
    public void initialise() {
        int length = 8000;
        float[] buffer = new float[length];
        mSignalBufferInterface = mGraphSignalInputInterface.addInput(length, length);

        for(int i = 0; i < length; i++) {
            buffer[i] = (float) i / (float) length;
        }

        //noinspection ConstantConditions
        if(mSignalBufferInterface != null) {
            mSignalBufferInterface.setBuffer(buffer);
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
