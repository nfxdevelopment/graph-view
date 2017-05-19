package com.nfx.android.graph.graphbufferinput.windowing;

/**
 * NFX Development
 * Created by nick on 26/12/16.
 */
public class NoWindow extends Window {
    @Override
    public float[] applyWindow(float[] buffer) {
        return buffer;
    }
}
