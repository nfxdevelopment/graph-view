package com.nfx.android.graph.graphbufferinput.windowing;

/**
 * NFX Development
 * Created by nick on 26/12/16.
 */
public class WeedonGaussWindow extends Window {

    @Override
    public float[] applyWindow(float[] buffer) {
        int length = buffer.length;
        double k = (-250.0 * 0.4605) / (double) (length * length);
        double d = (double) length / 2.0;

        for(int i = 0; i < length; ++i) {
            double n = (double) i - d;
            buffer[i] *= (float) Math.exp(n * n * k);
        }

        return buffer;
    }
}
