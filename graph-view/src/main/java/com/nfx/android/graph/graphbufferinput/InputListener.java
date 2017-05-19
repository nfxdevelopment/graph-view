package com.nfx.android.graph.graphbufferinput;

/**
 * NFX Development
 * Created by nick on 18/08/16.
 */
public abstract class InputListener {
    public abstract void inputBlockSizeUpdate(int blockSize);

    public abstract void bufferUpdate(float[] buffer);

    public abstract void inputRemoved();
}
