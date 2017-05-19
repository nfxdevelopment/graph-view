package com.nfx.android.graph.graphbufferinput.windowing;

/**
 * NFX Development
 * Created by nick on 26/12/16.
 */
public class BlackmanHarrisWindow extends Window {

    // Blackman-Harris coefficients.  These sum to 1.0.
    private static final double BH_A0 = 0.35875;
    private static final double BH_A1 = 0.48829;
    private static final double BH_A2 = 0.14128;
    private static final double BH_A3 = 0.01168;

    @Override
    public float[] applyWindow(float[] buffer) {
        int length = buffer.length;
        double n = (double) (length - 1);
        for(int i = 0; i < length; ++i) {
            double f = Math.PI * (double) i / n;
            buffer[i] *= (float) (BH_A0 -
                    BH_A1 * Math.cos(2.0 * f) +
                    BH_A2 * Math.cos(4.0 * f) -
                    BH_A3 * Math.cos(6.0 * f));
        }

        return buffer;
    }
}
