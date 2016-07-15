package com.nfx.android.library.graphbufferinput;

import com.nfx.android.library.androidgraph.GraphManager;

import org.jtransforms.fft.FloatFFT_1D;

/**
 * NFX Development
 * Created by nick on 30/11/15.
 *
 * This class takes the microphone input and computes the fft of signal. In addition to this the
 * final buffer is logarithmic
 */
public class MicrophoneFFTInput extends MicrophoneInput {

    // NW pulled from other magnitude scales. This will ensure a signal of +1 to -1 is equal to 0db
    // This fudge factor is added to the output to make a realistically
    // fully-saturated signal come to 0dB.  Without it, the signal would
    // have to be solid samples of -32768 to read zero, which is not
    // realistic.  This really is a fudge, because the best value depends
    // on the input frequency and sampling rate.  We optimise here for
    // a 1kHz signal at 16,000 samples/sec.
    private static final float FUDGE = 0.63610f;
    /**
     * Number of historical buffers to store
     */
    private int sNumberOfHistoryBuffers = 4;
    /**
     * Computes the FFT
     */
    private FloatFFT_1D fftCalculations = null;
    /**
     * Buffer to pass to the fft class
     */
    private float[] fftBuffer;
    /**
     * Buffer with the finished data in
     */
    private float[] returnedMagnitudeBuffer;
    /**
     * Last fft buffer to be converted
     */
    private float[] mMagnitudeBuffer;
    /**
     * Stores a history of the previous buffers
     */
    private float[][] mHistoryMagnitudeBuffers;
    /**
     * Current history buffer to write into
     */
    private int mHistoryIndex = 0;

    /**
     * Constructor to initialise microphone for listening
     *
     * @param graphSignalInputInterface interface to send signal data to
     * @param binSize set the bin size of the fft
     */
    protected MicrophoneFFTInput(GraphManager.GraphSignalInputInterface
                                         graphSignalInputInterface, int binSize) {
        super(graphSignalInputInterface, binSize);
    }

    @Override
    public void initialise() {
        super.initialise();

        // We want to remove the original signal from microphone input and add a new fft one
        mSignalBuffers.removedSignalBuffer(0);
        mGraphSignalInputInterface.removeSignalBuffer(0);
        mSignalBuffers.addSignalBuffer(0, mInputBlockSize / 2, getSampleRate() / 2,
                GraphManager.Scale.logarithmic);
        mGraphSignalInputInterface.setSignalBuffers(mSignalBuffers);

        fftCalculations = new FloatFFT_1D(mInputBlockSize);

        fftBuffer = new float[mInputBlockSize * 2];
        mMagnitudeBuffer = new float[mInputBlockSize / 2];
        returnedMagnitudeBuffer = new float[mInputBlockSize / 2];

        mHistoryMagnitudeBuffers = new float[sNumberOfHistoryBuffers][mInputBlockSize / 2];
    }

    /**
     * This takes the last read buffer and does a FFT calculation on it. It then converts the values
     * into dB. This may take a while so we have to optimise this as much as possible
     *
     * @param buffer Buffer containing the data.
     */
    @Override
    protected void readDone(float[] buffer) {
        if(mGraphSignalInputInterface != null) {
            applyHanningWindow(buffer);
            System.arraycopy(buffer, 0, fftBuffer, 0, buffer.length);
            fftCalculations.realForwardFull(fftBuffer);

            float real, imaginary;

            int bufferLength = mMagnitudeBuffer.length;

            for(int i = 0; i < bufferLength; ++i) {
                real = fftBuffer[i * 2];
                imaginary = fftBuffer[i * 2 + 1];
                final float scale = buffer.length * FUDGE;
                mMagnitudeBuffer[i] = (float) Math.sqrt(real * real + imaginary * imaginary) /
                        scale;

                mMagnitudeBuffer[i] = 10f * (float) Math.log10(mMagnitudeBuffer[i]);
                mMagnitudeBuffer[i] *= -0.01;
                mMagnitudeBuffer[i] = 1f - mMagnitudeBuffer[i];
            }

            mMagnitudeBuffer[0] = mMagnitudeBuffer[1];

            applyingFFTAveraging();
            postBufferChange();
        }
    }

    /**
     * takes in a buffer and applies a hanning window to it.
     *
     * @param buffer buffer to apply the hanning window to
     */
    private void applyHanningWindow(float[] buffer) {
        int bufferLength = buffer.length;
        double twoPi = 2.0 * Math.PI;

        for(int n = 1; n < bufferLength; n++) {
            buffer[n] *= 0.5 * (1 - Math.cos((twoPi * n) / (bufferLength - 1)));
        }
    }

    /**
     * Averages the new buffer with the old buffers and stores the results the return buffer
     */
    private void applyingFFTAveraging() {
        // Update the index.
        if(++mHistoryIndex >= sNumberOfHistoryBuffers) {
            mHistoryIndex = 0;
        }

        int bufferLength = mMagnitudeBuffer.length;

        System.arraycopy(
                mMagnitudeBuffer, 0, mHistoryMagnitudeBuffers[mHistoryIndex], 0, bufferLength);

        for(int i = 0; i < bufferLength; ++i) {
            returnedMagnitudeBuffer[i] = 0;
            for(int g = 0; g < sNumberOfHistoryBuffers; ++g) {
                returnedMagnitudeBuffer[i] += mHistoryMagnitudeBuffers[g][i];
            }
            returnedMagnitudeBuffer[i] /= sNumberOfHistoryBuffers;
        }
    }

    /**
     * When the return buffer is ready to go this function is called
     */
    private void postBufferChange() {
        mSignalBuffers.getSignalBuffer().get(0).setBuffer(returnedMagnitudeBuffer);
    }

    public void setNumberOfHistoryBuffers(int sNumberOfHistoryBuffers) {
        this.sNumberOfHistoryBuffers = sNumberOfHistoryBuffers;

        mHistoryMagnitudeBuffers = new float[sNumberOfHistoryBuffers][mInputBlockSize / 2];
    }

}
