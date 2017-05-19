package com.nfx.android.graph.graphbufferinput.windowing;

/**
 * NFX Development
 * Created by nick on 26/12/16.
 */
public class GaussWindow extends Window {
    @Override
    public float[] applyWindow(float[] buffer) {
        int length = buffer.length;
        double k = (double) (length - 1) / 2;

        for(int i = 0; i < length; ++i) {
            double d = (i - k) / (0.4 * k);
            buffer[i] *= (float) Math.exp(-0.5 * d * d);
        }

        return buffer;
    }
}
