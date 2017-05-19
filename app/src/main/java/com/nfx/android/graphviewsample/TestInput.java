package com.nfx.android.graphviewsample;

import android.support.annotation.Nullable;

import com.nfx.android.graph.graphbufferinput.Input;
import com.nfx.android.graph.graphbufferinput.TriggerDetection;

/**
 * NFX Development
 * Created by nick on 14/12/15.
 */
class TestInput extends Input {

    private float[] mBuffer;

    @Override
    public void initialise() {
        setLength(getBufferSize());
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

    @Override
    public int getSampleRate() {
        return 0;
    }

    @Override
    public void setSampleRate(int sampleRate) throws Exception {
        // N/a
    }

    @Override
    public int getBufferSize() {
        return 20;
    }

    @Override
    public void setBufferSize(int bufferSize) {
        // N/a
    }

    @Override
    public boolean hasTriggerDetection() {
        return false;
    }

    @Nullable
    @Override
    public TriggerDetection getTriggerDetection() {
        return null;
    }

    void generateRandomBufferInput() {
        final int length = mBuffer.length;
        for(int i = 0; i < length; i++) {
            mBuffer[i] = (float) Math.random();
        }

        notifyListenersOfBufferChange(mBuffer);
    }

    int getLength() {
        return mBuffer.length;
    }

    private void setLength(int length) {
        mBuffer = new float[length];
        notifyListenersOfInputBlockSizeChange(length);
    }

    float[] getBuffer() {
        return mBuffer;
    }

}
