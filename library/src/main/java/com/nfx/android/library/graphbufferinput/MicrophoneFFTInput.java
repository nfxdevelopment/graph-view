package com.nfx.android.library.graphbufferinput;

import com.nfx.android.library.androidgraph.GraphManager;
import com.nfx.android.library.androidgraph.SignalBuffer;

import org.jtransforms.fft.FloatFFT_1D;

/**
 * NFX Development
 * Created by nick on 30/11/15.
 */
public class MicrophoneFFTInput extends MicrophoneInput {

    // Maximum signal amplitude for 16-bit data.
    private static final float MAX_16_BIT = 32768;
    FloatFFT_1D fftCalculations = new FloatFFT_1D(inputBlockSize);
    float[] fftBuffer;
    float[] magnitudeBuffer;

    /**
     * Constructor to initialise microphone for listening
     *
     * @param graphSignalInputInterface interface to send signal data to
     */
    protected MicrophoneFFTInput(GraphManager.GraphSignalInputInterface graphSignalInputInterface) {
        super(graphSignalInputInterface);
        // We want to remove the orignal signal from microphone input and add a new fft one
        mSignalBuffers.removedSignalBuffer(0);
        mGraphSignalInputInterface.removeSignalBuffer(0);
        mSignalBuffers.addSignalBuffer(0, inputBlockSize / 2, SignalBuffer.SignalScale.logarithmic);
        mGraphSignalInputInterface.setSignalBuffers(mSignalBuffers);

        fftBuffer = new float[inputBlockSize * 2];
        magnitudeBuffer = new float[inputBlockSize / 2];

    }

    @Override
    protected void readDone(float[] buffer) {
        if(mGraphSignalInputInterface != null) {
            applyHanningWindow(buffer);
            System.arraycopy(buffer, 0, fftBuffer, 0, buffer.length);
            fftCalculations.realForwardFull(fftBuffer);

            float real, imaginary;
            for(int i = 1; i < magnitudeBuffer.length; ++i) {
                real = fftBuffer[i * 2];
                imaginary = fftBuffer[i * 2 - 1];
                magnitudeBuffer[i] = (float) Math.sqrt(real * real + imaginary * imaginary);

                magnitudeBuffer[i] = 10f * (float) Math.log10(magnitudeBuffer[i]);
                magnitudeBuffer[i] *= -0.01;

            }

            magnitudeBuffer[0] = magnitudeBuffer[1];

            mSignalBuffers.getSignalBuffer().get(0).setBuffer(magnitudeBuffer);
        }
    }

    public void applyHanningWindow(float[] buffer) {
        int bufferLength = buffer.length;
        double twoPi = 2.0 * Math.PI;

        for(int n = 1; n < bufferLength; n++) {
            buffer[n] *= 0.5 * (1 - Math.cos((twoPi * n) / (bufferLength - 1)));
        }
    }
}
