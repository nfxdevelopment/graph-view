package com.nfx.android.graph.graphbufferinput;

/**
 * NFX Development
 * Created by nick on 14/12/15.
 */
@SuppressWarnings("unused")
class TestInput extends Input {

    private float[] mBuffer;

    @Override
    public void initialise() {
        setLength(8000);
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

    void setLength(int length) {
        mBuffer = new float[length];
        notifyListenersOfInputBlockSizeChange(length);
    }

    float[] getBuffer() {
        return mBuffer;
    }

}
