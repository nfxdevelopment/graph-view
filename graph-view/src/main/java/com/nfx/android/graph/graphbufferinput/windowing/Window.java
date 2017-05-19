package com.nfx.android.graph.graphbufferinput.windowing;

/**
 * NFX Development
 * Created by nick on 26/12/16.
 */
// TODO Add another windowing technique
public abstract class Window {
    public abstract float[] applyWindow(float[] buffer);
}
