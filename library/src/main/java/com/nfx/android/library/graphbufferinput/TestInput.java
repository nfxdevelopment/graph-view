package com.nfx.android.library.graphbufferinput;

import com.nfx.android.library.androidgraph.GraphManager;
import com.nfx.android.library.androidgraph.SignalBuffer;

/**
 * NFX Development
 * Created by nick on 14/12/15.
 */
public class TestInput extends Input {

    public TestInput(GraphManager.GraphSignalInputInterface graphSignalInputInterface) {
        super(graphSignalInputInterface);

        initialise();
    }

    @Override
    public void initialise() {
        int length = 8000;
        float[] buffer = new float[length];
        mSignalBuffers.addSignalBuffer(0, length, length, SignalBuffer.SignalScale.logarithmic);
        mGraphSignalInputInterface.setSignalBuffers(mSignalBuffers);

        for(int i = 0; i < length; i++) {
            buffer[i] = (float) i / (float) length;
        }

        if(mGraphSignalInputInterface != null) {
            mSignalBuffers.getSignalBuffer().get(0).setBuffer(buffer);
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
