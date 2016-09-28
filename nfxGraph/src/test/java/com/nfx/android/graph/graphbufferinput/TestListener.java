package com.nfx.android.graph.graphbufferinput;

/**
 * NFX Development
 * Created by nick on 23/09/16.
 */
class TestListener extends InputListener {
    private float[] mBuffer;

    @Override
    public void inputBlockSizeUpdate(int blockSize) {
        mBuffer = new float[blockSize];
    }

    @Override
    public void bufferUpdate(float[] buffer) {
        mBuffer = buffer;
    }

    @Override
    public void inputRemoved() {
        mBuffer = null;
    }

    int getLength() {
        return mBuffer.length;
    }

    float[] getBuffer() {
        return mBuffer;
    }
}
