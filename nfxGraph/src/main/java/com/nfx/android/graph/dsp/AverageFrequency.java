package com.nfx.android.graph.dsp;

/**
 * Takes a buffer in and looks at the zero crossing points
 * and works the frequency from that
 *
 * @author nick
 */
public class AverageFrequency {
    private final static float ZERO_CROSSING_POINT = 0.5f;
    private int frequency = 1;
    private int sampleRate;

    public AverageFrequency(int SampleRate) {
        this.sampleRate = SampleRate;
    }

    public int analyseBuffer(float[] buffer) {
        int bufferLength = buffer.length;
        boolean isPositiveSign1;
        boolean isPositiveSign2;
        int numberOfZeroCrossingPoints = 0;
        int indexOfFirstZeroCrossingPoint = 0;
        int indexOfLastZeroCrossingPoint = sampleRate;

        int i = 0;
        for(; i < bufferLength - 1; i++) {
            isPositiveSign1 = isPositiveNumber(buffer[i]);
            isPositiveSign2 = isPositiveNumber(buffer[i + 1]);
            if(isPositiveSign1 != isPositiveSign2) {
                indexOfFirstZeroCrossingPoint = i;
                break;
            }
        }

        for(; i < bufferLength - 1; i++) {
            isPositiveSign1 = isPositiveNumber(buffer[i]);
            isPositiveSign2 = isPositiveNumber(buffer[i + 1]);
            if(isPositiveSign1 != isPositiveSign2) {
                numberOfZeroCrossingPoints++;
                indexOfLastZeroCrossingPoint = i;
            }
        }

        int numberOfSamplesBetweenFirstAndLast = indexOfLastZeroCrossingPoint -
                indexOfFirstZeroCrossingPoint;
        frequency = (numberOfZeroCrossingPoints * sampleRate) / numberOfSamplesBetweenFirstAndLast;

        return frequency;
    }

    private boolean isPositiveNumber(float data) {
        return data >= ZERO_CROSSING_POINT;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setSampleRate(int SampleRate) {
        sampleRate = SampleRate;
    }

}