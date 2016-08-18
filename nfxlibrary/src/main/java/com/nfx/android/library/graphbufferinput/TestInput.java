package com.nfx.android.library.graphbufferinput;

import com.nfx.android.library.androidgraph.AxisScale.AxisParameters;
import com.nfx.android.library.androidgraph.GraphView;
import com.nfx.android.library.androidgraph.Scale;

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
        mSignalBufferInterface = mGraphSignalInputInterface.addInput(length,
                new AxisParameters(0, length, Scale.linear));

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
