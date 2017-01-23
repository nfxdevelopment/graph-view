package com.nfx.android.graph.graphbufferinput;

import com.nfx.android.graph.androidgraph.GraphViewInterface;
import com.nfx.android.graph.graphbufferinput.windowing.HannWindow;
import com.nfx.android.graph.graphbufferinput.windowing.Window;

import org.jtransforms.fft.FloatFFT_1D;

/**
 * NFX Development
 * Created by nick on 30/11/15.
 * <p/>
 * This class takes the microphone input and computes the fft of signal. In addition to this the
 * final buffer is logarithmic
 */
public class MicrophoneFFTInput extends MicrophoneInput implements MicrophoneFFTInputInterface {

    // NW pulled from other magnitude scales. This will ensure a signal of +1 to -1 is equal to 0db
    // This fudge factor is added to the output to make a realistically
    // fully-saturated signal come to 0dB.  Without it, the signal would
    // have to be solid samples of -32768 to read zero, which is not
    // realistic.  This really is a fudge, because the best value depends
    // on the input frequency and sampling rate.  We optimise here for
    // a 1kHz signal at 16,000 samples/sec.
    private static final float FUDGE = 0.63610f;
    /**
     * The interface in which to send updates to
     */
    private final GraphViewInterface graphViewInterface;
    /**
     * Buffer to pass to the fft class
     */
    protected float[] fftBuffer;
    /**
     * Last fft buffer to be converted
     */
    protected float[] magnitudeBuffer;
    /**
     * Number of historical buffers to store
     */
    private int numberOfHistoryBuffers = 4;
    /**
     * Computes the FFT
     */
    private FloatFFT_1D fftCalculations = null;
    /**
     * Buffer with the finished data in
     */
    private float[] returnedMagnitudeBuffer;
    /**
     * Stores a history of the previous buffers
     */
    private float[][] historyMagnitudeBuffers;
    /**
     * Current history buffer to write into
     */
    private int historyIndex = 0;
    /**
     * Window to apply to signal prior to FFT
     */
    private Window window = new HannWindow();

    /**
     * Constructor to initialise microphone for listening
     *
     * @param graphViewInterface interface to graph information
     * @param binSize set the bin size of the fft
     */
    public MicrophoneFFTInput(GraphViewInterface graphViewInterface,
                              int binSize) {
        super(binSize * 2);
        this.graphViewInterface = graphViewInterface;
    }

    @Override
    public synchronized void start() throws RuntimeException {
        super.start();

        fftCalculations = new FloatFFT_1D(inputBlockSize);

        fftBuffer = new float[inputBlockSize * 2];
        magnitudeBuffer = new float[inputBlockSize / 2];
        returnedMagnitudeBuffer = new float[inputBlockSize / 2];

        historyMagnitudeBuffers = new float[numberOfHistoryBuffers][inputBlockSize / 2];

        notifyListenersOfInputBlockSizeChange(inputBlockSize / 2);

    }

    @Override
    public int getBufferSize() {
        return inputBlockSize / 2;
    }

    @Override
    public void setBufferSize(int bufferSize) {
        setInputBlockSize(bufferSize * 2);
    }

    /**
     * This takes the last read buffer and does a FFT calculation on it. It then converts the values
     * into dB. This may take a while so we have to optimise this as much as possible
     *
     * @param buffer Buffer containing the data.
     */
    @Override
    protected synchronized void readDone(float[] buffer) {
        if(isRunning()) {
            applyMagnitudeConversions(buffer);
            applyingFFTAveraging();
            notifyListenersOfBufferChange(returnedMagnitudeBuffer);
        }
    }

    protected void applyMagnitudeConversions(float buffer[]) {
        if(graphViewInterface != null) {
            buffer = window.applyWindow(buffer);
            System.arraycopy(buffer, 0, fftBuffer, 0, buffer.length);
            fftCalculations.realForwardFull(fftBuffer);

            float real, imaginary;

            int bufferLength = magnitudeBuffer.length;

            for(int i = 0; i < bufferLength; ++i) {
                real = fftBuffer[i * 2];
                imaginary = fftBuffer[i * 2 + 1];
                final float scale = buffer.length * FUDGE;
                magnitudeBuffer[i] = (float) Math.sqrt(real * real + imaginary * imaginary) /
                        scale;

                // Convert the signal into decibels so it is easier to read on screen.
                // 20*log(value) / scaledToAxisMinimum
                // Then flip the buffer to allow simple display on screen. (Screens display top to
                // bottom, graphs show bottom to top)
                magnitudeBuffer[i] = 20f * (float) Math.log10(magnitudeBuffer[i]);
                magnitudeBuffer[i] /= graphViewInterface.getGraphParameters().
                        getYAxisParameters().getMinimumValue(); // Scale to negative 140 db
                magnitudeBuffer[i] = 1f - magnitudeBuffer[i];
            }
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
        if(++historyIndex >= numberOfHistoryBuffers) {
            historyIndex = 0;
        }

        int bufferLength = magnitudeBuffer.length;

        System.arraycopy(
                magnitudeBuffer, 0, historyMagnitudeBuffers[historyIndex], 0, bufferLength);

        for(int i = 0; i < bufferLength; ++i) {
            returnedMagnitudeBuffer[i] = 0;
            for(int g = 0; g < numberOfHistoryBuffers; ++g) {
                returnedMagnitudeBuffer[i] += historyMagnitudeBuffers[g][i];
            }
            returnedMagnitudeBuffer[i] /= numberOfHistoryBuffers;
        }
    }

    @Override
    public int getNumberOfHistoryBuffers() {
        return this.numberOfHistoryBuffers;
    }

    @Override
    public void setNumberOfHistoryBuffers(int numberOfHistoryBuffers) {
        this.numberOfHistoryBuffers = numberOfHistoryBuffers;

        historyMagnitudeBuffers = new float[numberOfHistoryBuffers][inputBlockSize / 2];
    }

    @Override
    public Window getWindow() {
        return window;
    }

    @Override
    public void setWindow(Window window) {
        this.window = window;
    }

    @Override
    public boolean hasTriggerDetection() {
        return false;
    }

    @Override
    public TriggerDetection getTriggerDetection() {
        return null;
    }
}
