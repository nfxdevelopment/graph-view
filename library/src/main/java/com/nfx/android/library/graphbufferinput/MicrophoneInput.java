package com.nfx.android.library.graphbufferinput;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.nfx.android.library.androidgraph.GraphManager;
import com.nfx.android.library.androidgraph.SignalBuffer;
import com.nfx.android.library.androidgraph.ZoomDisplay;

/**
 * NFX Development
 * Created by nick on 10/11/15.
 * <p/>
 * Sets up the microphone for listening, the data taken and sent on to the interface
 * The touch events are handled by this object to manipulate the microphone input
 */
public class MicrophoneInput extends Input {
    private final static String TAG = "MicrophoneInput";
    // The desired sampling rate for this analyser, in samples/sec.
    private static final int sampleRate = 8000;
    /**
     * Audio buffer size calculated at runtime
     */
    private static final int sAudioBufferSize = AudioRecord.getMinBufferSize(sampleRate,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT);
    private static final float sMinimumSpan = 200f;
    // Audio input block size, in samples.
    private final int inputBlockSize = 256;
    /**
     * Audio input device
     */
    private AudioRecord audioInput;
    // Flag whether the thread should be running.
    private boolean running = false;
    // The thread, if any, which is currently reading.  Null if not running.
    private Thread readerThread = null;
    private boolean mPaused = false;
    private DisplayMetrics mDisplayMetrics;
    private float lastSpanX;
    private float lastSpanY;

    /**
     * Constructor to initialise microphone for listening
     *
     * @param graphSignalInputInterface interface to send signal data to
     */
    protected MicrophoneInput(GraphManager.GraphSignalInputInterface graphSignalInputInterface) {
        super(graphSignalInputInterface);
        mGraphSignalInputInterface = graphSignalInputInterface;
        mSignalBuffers.addSignalBuffer(0, inputBlockSize, SignalBuffer.SignalScale.linear);
        mGraphSignalInputInterface.setSignalBuffers(mSignalBuffers);
        initialise();
    }

    @Override
    public void initialise() {
        // Set up the audio input.
        AudioFormat audioFormat = new AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                .setSampleRate(sampleRate)
                .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                .build();
        audioInput = new AudioRecord.Builder()
                .setAudioSource(MediaRecorder.AudioSource.MIC)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(inputBlockSize)
                .build();

    }

    @Override
    public void start() {
        running = true;
        readerThread = new Thread(new Runnable() {
            public void run() {
                readerRun();
            }
        }, "Audio Reader");

        readerThread.start();
    }

    @Override
    public void stop() {
        running = false;
        try {
            if (readerThread != null)
                readerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        readerThread = null;

        // Kill the audio input.
        if (audioInput != null) {
            audioInput.release();
            audioInput = null;
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * Main loop of the audio reader.  This runs in our own thread.
     */
    private void readerRun() {
        float[] buffer = new float[inputBlockSize];

        try {
            Log.i(TAG, "Reader: Start Recording");
            audioInput.startRecording();
            while (running) {

                int bytesRead = audioInput.read(buffer, 0, inputBlockSize, AudioRecord
                        .READ_BLOCKING);

                if (bytesRead < 0) {
                    Log.e(TAG, "Audio read failed: error " + bytesRead);
                    running = false;
                    break;
                }

                if (!mPaused) {
                    readDone(buffer);
                }
            }
        } finally {
            if (audioInput.getState() == AudioRecord.RECORDSTATE_RECORDING)
                audioInput.stop();
        }
    }

    /**
     * Notify the client that a read has completed.
     *
     * @param buffer Buffer containing the data.
     */
    protected void readDone(float[] buffer) {
        if (mGraphSignalInputInterface != null) {
            mSignalBuffers.getSignalBuffer().get(0).setBuffer(buffer);
        }
    }

    @Override
    public void surfaceChanged(DisplayMetrics displayMetrics) {
        mDisplayMetrics = displayMetrics;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onSingleTap(MotionEvent e) {
        mPaused = !mPaused;
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        scrollHandle(getZoomDisplayX(), distanceX, mDisplayMetrics.widthPixels);
        scrollHandle(getZoomDisplayY(), distanceY, mDisplayMetrics.heightPixels);

        return true;
    }

    private void scrollHandle(ZoomDisplay zoomDisplay, float distanceMoved, float displaySize) {
        float displayOffset = zoomDisplay.getDisplayOffsetPercentage();

        float movementPercentage = (distanceMoved / displaySize) *
                zoomDisplay.getZoomLevelPercentage();

        zoomDisplay.setDisplayOffsetPercentage(displayOffset + movementPercentage);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        lastSpanX = scaleGestureDetector.getCurrentSpanX();
        lastSpanY = scaleGestureDetector.getCurrentSpanY();

        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {

        // Deal with the span changes for x and y. This is only based on the previous span
        float currentSpanX = scaleGestureDetector.getCurrentSpanX();
        if (currentSpanX > sMinimumSpan) {
            float scaleFactorX = lastSpanX / currentSpanX;
            lastSpanX = currentSpanX;
            scaleHandle(getZoomDisplayX(), scaleFactorX);

            // We want to span from the focal point of the users fingers, use display offset to
            // shift
            // the signal over whilst scaling
            float focusX = scaleGestureDetector.getFocusX();
            offsetAccountedScaleHandle(getZoomDisplayX(), scaleFactorX, focusX,
                    mDisplayMetrics.widthPixels);
        }

        float currentSpanY = scaleGestureDetector.getCurrentSpanY();
        if (currentSpanY > sMinimumSpan) {
            float scaleFactorY = lastSpanY / currentSpanY;
            lastSpanY = currentSpanY;
            scaleHandle(getZoomDisplayY(), scaleFactorY);

            float focusY = scaleGestureDetector.getFocusY();

            offsetAccountedScaleHandle(getZoomDisplayY(), scaleFactorY, focusY,
                    mDisplayMetrics.heightPixels);
        }

        return true;
    }

    private void scaleHandle(ZoomDisplay zoomDisplay, float scaleFactor) {
        float displayZoom = zoomDisplay.getZoomLevelPercentage();

        float zoomPercentage = displayZoom * scaleFactor;

        zoomDisplay.setZoomLevelPercentage(zoomPercentage);
    }

    private void offsetAccountedScaleHandle(ZoomDisplay zoomDisplay, float scaleFactor, float
            focusPoint,
                                            float displaySize) {
        float displayOffset = zoomDisplay.getDisplayOffsetPercentage();
        float displayFarSide = zoomDisplay.getFarSideOffsetPercentage();
        //Calculate the focal point of the users finger based on the whole signal
        float focusPointPercentage = displayOffset + ((displayFarSide - displayOffset) *
                (focusPoint / displaySize));

        float newOffset = displayOffset + ((focusPointPercentage - displayOffset) *
                (1 - scaleFactor));

        zoomDisplay.setDisplayOffsetPercentage(newOffset);
    }

    private ZoomDisplay getZoomDisplayX() {
        return mSignalBuffers.getSignalBuffer().get(0).getXZoomDisplay();
    }

    private ZoomDisplay getZoomDisplayY() {
        return mSignalBuffers.getSignalBuffer().get(0).getYZoomDisplay();
    }
}
