package com.nfx.android.library.graphbufferinput;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.nfx.android.library.androidgraph.GraphView;

/**
 * NFX Development
 * Created by nick on 10/11/15.
 * <p/>
 * Sets up the microphone for listening, the data taken and sent on to the interface
 * The touch events are handled by this object to manipulate the microphone input
 */
public abstract class MicrophoneInput extends Input {
    /**
     * The desired sampling rate for this analyser, in samples/sec.
     */
    static final int SAMPLE_RATE = 48000;
    private final static String TAG = "MicrophoneInput";
    /**
     * Audio input block size, in samples.
     */
    int mInputBlockSize = 2048;
    /**
     * Audio input device
     */
    private AudioRecord mAudioInput;
    /**
     * Flag whether the thread should be mRunning.
     */
    private boolean mRunning = false;
    /**
     * The thread, if any, which is currently reading.  Null if not mRunning
     */
    private Thread mReaderThread = null;

    /**
     * @param graphSignalInputInterface interface to send signal data to
     */
    @SuppressWarnings("WeakerAccess")
    protected MicrophoneInput(GraphView.GraphSignalInputInterface graphSignalInputInterface) {
        super(graphSignalInputInterface);
    }

    /**
     * @param graphSignalInputInterface interface to send signal data to
     * @param inputBlockSize            initial blockSize
     */
    MicrophoneInput(GraphView.GraphSignalInputInterface graphSignalInputInterface,
                    int inputBlockSize) {
        this(graphSignalInputInterface);
        this.mInputBlockSize = inputBlockSize;
    }

    @Override
    public void initialise() {
        final int audioBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT);

        if(mInputBlockSize < audioBufferSize) {
            mInputBlockSize = audioBufferSize;
        }
    }

    @Override
    public void start() {
        // Set up the audio input.
        AudioFormat audioFormat = new AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                .setSampleRate(SAMPLE_RATE)
                .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                .build();
        mAudioInput = new AudioRecord.Builder()
                .setAudioSource(MediaRecorder.AudioSource.MIC)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(mInputBlockSize)
                .build();

        mRunning = true;
        mReaderThread = new Thread(new Runnable() {
            public void run() {
                readerRun();
            }
        }, "Audio Reader");

        mReaderThread.start();
    }

    @Override
    public void stop() {
        mRunning = false;
        try {
            if(mReaderThread != null)
                mReaderThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mReaderThread = null;

        // Kill the audio input.
        if(mAudioInput != null) {
            mAudioInput.release();
            mAudioInput = null;
        }
    }

    /**
     * Main loop of the audio reader.  This runs in our own thread.
     */
    private void readerRun() {
        float[] buffer = new float[mInputBlockSize];

        Log.i(TAG, "Reader: Start Recording");
        mAudioInput.startRecording();
        while(mRunning) {

            int bytesRead = mAudioInput.read(buffer, 0, mInputBlockSize, AudioRecord
                    .READ_BLOCKING);

            if(bytesRead < 0) {
                Log.e(TAG, "Audio read failed: error " + bytesRead);
                mRunning = false;
                break;
            }

            if(!mPaused) {
                readDone(buffer);
            }
        }

        if(mAudioInput.getState() == AudioRecord.RECORDSTATE_RECORDING)
            mAudioInput.stop();
    }

    /**
     * Notify the client that a read has completed.
     *
     * @param buffer Buffer containing the data.
     */
    protected void readDone(float[] buffer) {
        notifyListenersOfBufferChange(buffer);
    }

    /**
     * @return current sample rate
     */
    public int getSampleRate() {
        return SAMPLE_RATE;
    }

    /**
     * @return current input block size
     */
    public int getInputBlockSize() {
        return mInputBlockSize;
    }

    /**
     * Set the block size for the audio input. The audio stream will be restarted if running.
     * If the block size is set lower than is possible by the device. The minimum block size is used
     *
     * @param inputBlockSize block size to set to
     */
    public void setInputBlockSize(int inputBlockSize) {
        boolean running = mRunning;

        final int audioBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT);

        if(running) {
            stop();
        }

        if(inputBlockSize < audioBufferSize) {
            mInputBlockSize = audioBufferSize;
        } else {
            mInputBlockSize = inputBlockSize;
        }

        notifyListenersOfInputBlockSizeChange(mInputBlockSize);

        initialise();

        if(running) {
            start();
        }
    }

    /**
     * @return is the audio capture thread running
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isRunning() {
        return mRunning;
    }

}
