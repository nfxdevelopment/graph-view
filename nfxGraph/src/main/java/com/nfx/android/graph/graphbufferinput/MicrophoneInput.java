package com.nfx.android.graph.graphbufferinput;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

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
    private static final int SAMPLE_RATE = 48000;
    private final static String TAG = "MicrophoneInput";
    @SuppressWarnings("FieldCanBeLocal")
    private final int mChannelConfig = AudioFormat.CHANNEL_IN_MONO;
    /**
     * Audio input block size, in samples.
     */
    protected int mInputBlockSize = 2048;
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
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    @SuppressWarnings("FieldCanBeLocal")
    private int mBufferSizeInBytes = 0;

    /**
     * @param inputBlockSize            initial blockSize
     */
    public MicrophoneInput(int inputBlockSize) {
        super();
        this.mInputBlockSize = inputBlockSize;

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            mAudioFormat = AudioFormat.ENCODING_PCM_FLOAT;
        } else {
            mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
        }
    }

    @Override
    public void initialise() {
    }

    @Override
    public void start() {
        final int audioBufferSizeInBytes = AudioRecord.getMinBufferSize(SAMPLE_RATE, mChannelConfig,
                mAudioFormat);

        if(mAudioFormat == AudioFormat.ENCODING_PCM_FLOAT) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mInputBlockSize = Math.max(mInputBlockSize, audioBufferSizeInBytes / 4);
                mBufferSizeInBytes = mInputBlockSize * 4;
            } else {
                throw new RuntimeException("ENCODING_PCM_FLOAT is not supported below Android" +
                        " Version 6.0");
            }
        } else if(mAudioFormat == AudioFormat.ENCODING_PCM_16BIT) {
            mInputBlockSize = Math.max(mInputBlockSize, audioBufferSizeInBytes / 2);
            mBufferSizeInBytes = mInputBlockSize * 2;
        } else if(mAudioFormat == AudioFormat.ENCODING_PCM_8BIT) {
            mInputBlockSize = Math.max(mInputBlockSize, audioBufferSizeInBytes);
            mBufferSizeInBytes = mInputBlockSize;
        } else {
            throw new RuntimeException("Unrecognized Encoding format only ENCODING_PCM_FLOAT," +
                    " ENCODING_PCM_16BIT , ENCODING_PCM_8BIT is supported");
        }

        notifyListenersOfInputBlockSizeChange(mInputBlockSize);

        // Set up the audio input.
        mAudioInput = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, mChannelConfig,
                mAudioFormat, mBufferSizeInBytes);

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
        float[] bufferFloat = new float[mInputBlockSize];
        short[] bufferShort = new short[mInputBlockSize];
        byte[] bufferByte = new byte[mInputBlockSize];

        Log.i(TAG, "Reader: Start Recording");
        mAudioInput.startRecording();
        while(mRunning) {

            int bytesRead;
            if(mAudioInput.getAudioFormat() == AudioFormat.ENCODING_PCM_FLOAT) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    bytesRead = mAudioInput.read(bufferFloat, 0, mInputBlockSize, AudioRecord
                            .READ_BLOCKING);
                } else {
                    throw new RuntimeException("ENCODING_PCM_FLOAT is not supported below Android" +
                            " Version 6.0");
                }
            } else if(mAudioInput.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT) {
                bytesRead = mAudioInput.read(bufferShort, 0, mInputBlockSize);
                for(int i = 0; i < mInputBlockSize; i++) {
                    bufferFloat[i] = (float) bufferShort[i] / (float) Short.MAX_VALUE;
                }
            } else if(mAudioInput.getAudioFormat() == AudioFormat.ENCODING_PCM_8BIT) {
                bytesRead = mAudioInput.read(bufferByte, 0, mInputBlockSize);
                for(int i = 0; i < mInputBlockSize; i++) {
                    bufferFloat[i] = (float) bufferByte[i] / (float) Byte.MAX_VALUE;
                }
            } else {
                throw new RuntimeException("Unrecognized Encoding format only ENCODING_PCM_FLOAT," +
                        " ENCODING_PCM_16BIT , ENCODING_PCM_8BIT is supported");
            }

            if(bytesRead < 0) {
                Log.e(TAG, "Audio read failed: error " + bytesRead);
                mRunning = false;
                break;
            }

            if(!mPaused) {
                readDone(bufferFloat);
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

        if(running) {
            stop();
        }

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
