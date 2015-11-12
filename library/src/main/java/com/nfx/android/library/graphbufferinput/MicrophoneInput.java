package com.nfx.android.library.graphbufferinput;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.nfx.android.library.androidgraph.GraphManager;
import com.nfx.android.library.androidgraph.SignalBuffer;

/**
 * NFX Development
 * Created by nick on 10/11/15.
 * <p/>
 * Sets up the microphone for listening, the data taken and sent on to the interface
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
    /**
     * Audio input device
     */
    private AudioRecord audioInput;
    // Flag whether the thread should be running.
    private boolean running = false;
    // The thread, if any, which is currently reading.  Null if not running.
    private Thread readerThread = null;
    // Audio input block size, in samples.
    private int inputBlockSize = 256;

    /**
     * Constructor to initialise microphone for listening
     *
     * @param graphSignalInputInterface interface to send signal data to
     */
    public MicrophoneInput(GraphManager.GraphSignalInputInterface graphSignalInputInterface) {
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

                int nread = audioInput.read(buffer, 0, inputBlockSize, AudioRecord.READ_BLOCKING);

                if (nread < 0) {
                    Log.e(TAG, "Audio read failed: error " + nread);
                    running = false;
                    break;
                }

                readDone(buffer);
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
    public void readDone(float[] buffer) {
        if (mGraphSignalInputInterface != null) {
            mSignalBuffers.getSignalBuffer().get(0).setBuffer(buffer);
        }
    }

}
