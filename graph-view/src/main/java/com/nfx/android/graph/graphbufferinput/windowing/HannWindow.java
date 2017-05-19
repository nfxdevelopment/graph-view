package com.nfx.android.graph.graphbufferinput.windowing;

/**
 * NFX Development
 * Created by nick on 26/12/16.
 */
public class HannWindow extends Window {
    public float[] applyWindow(float[] buffer) {
        int bufferLength = buffer.length;
        double twoPi = 2.0 * Math.PI;

        for(int n = 1; n < bufferLength; n++) {
            buffer[n] *= 0.5 * (1 - Math.cos((twoPi * n) / (bufferLength - 1)));
        }

        return buffer;
    }
}
