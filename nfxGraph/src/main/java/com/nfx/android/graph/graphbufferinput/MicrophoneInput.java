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
    private final static String TAG = "MicrophoneInput";
    @SuppressWarnings("FieldCanBeLocal")
    private final int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    /**
     * Audio input block size, in samples.
     */
    int inputBlockSize = 2048;
    /**
     * The desired sampling rate for this analyser, in samples/sec.
     */
    private int sampleRate = 48000;
    /**
     * Audio input device
     */
    private AudioRecord audioInput;
    /**
     * The thread, if any, which is currently reading.  Null if not running
     */
    private Thread readerThread = null;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    @SuppressWarnings("FieldCanBeLocal")
    private int bufferSizeInBytes = 0;

    /**
     * @param inputBlockSize            initial blockSize
     */
    @SuppressWarnings("WeakerAccess")
    public MicrophoneInput(int inputBlockSize) {
        super();
        this.inputBlockSize = inputBlockSize;

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            audioFormat = AudioFormat.ENCODING_PCM_FLOAT;
        } else {
            audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        }
    }

    @Override
    public void initialise() {
    }

    @Override
    public void start() throws RuntimeException {
        // Only try and start if not running
        if(!running) {
            final int audioBufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRate,
                    channelConfig,
                    audioFormat);

            if(audioFormat == AudioFormat.ENCODING_PCM_FLOAT) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    inputBlockSize = Math.max(inputBlockSize, audioBufferSizeInBytes / 4);
                    bufferSizeInBytes = inputBlockSize * 4;
                } else {
                    throw new RuntimeException("ENCODING_PCM_FLOAT is not supported below Android" +
                            " Version 6.0");
                }
            } else if(audioFormat == AudioFormat.ENCODING_PCM_16BIT) {
                inputBlockSize = Math.max(inputBlockSize, audioBufferSizeInBytes / 2);
                bufferSizeInBytes = inputBlockSize * 2;
            } else if(audioFormat == AudioFormat.ENCODING_PCM_8BIT) {
                inputBlockSize = Math.max(inputBlockSize, audioBufferSizeInBytes);
                bufferSizeInBytes = inputBlockSize;
            } else {
                throw new RuntimeException("Unrecognized Encoding format only ENCODING_PCM_FLOAT," +
                        " ENCODING_PCM_16BIT , ENCODING_PCM_8BIT is supported");
            }

            notifyListenersOfInputBlockSizeChange(inputBlockSize);

            // Set up the audio input.
            try {
                audioInput = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
                        channelConfig,
                        audioFormat, bufferSizeInBytes);
            } catch(RuntimeException e) {
                throw new RuntimeException(e.getMessage());
            }

            running = true;
            readerThread = new Thread(new Runnable() {
                public void run() {
                    readerRun();
                }
            }, "Audio Reader");

            readerThread.start();
        }
    }

    @Override
    public void stop() {
        running = false;
        try {
            if(readerThread != null)
                readerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        readerThread = null;

        // Kill the audio input.
        if(audioInput != null) {
            audioInput.release();
            audioInput = null;
        }
    }

    /**
     * Main loop of the audio reader.  This runs in our own thread.
     */
    private void readerRun() {
        float[] bufferFloat = new float[inputBlockSize];
        short[] bufferShort = new short[inputBlockSize];
        byte[] bufferByte = new byte[inputBlockSize];

        Log.i(TAG, "Reader: Start Recording");
        audioInput.startRecording();
        while(running) {

            int unitsRead;
            if(audioInput.getAudioFormat() == AudioFormat.ENCODING_PCM_FLOAT) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    unitsRead = audioInput.read(bufferFloat, 0, inputBlockSize, AudioRecord
                            .READ_BLOCKING);
                } else {
                    throw new RuntimeException("ENCODING_PCM_FLOAT is not supported below Android" +
                            " Version 6.0");
                }
            } else if(audioInput.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT) {
                unitsRead = audioInput.read(bufferShort, 0, inputBlockSize);
                for(int i = 0; i < inputBlockSize; i++) {
                    bufferFloat[i] = (float) bufferShort[i] / (float) Short.MAX_VALUE;
                }
            } else if(audioInput.getAudioFormat() == AudioFormat.ENCODING_PCM_8BIT) {
                unitsRead = audioInput.read(bufferByte, 0, inputBlockSize);
                for(int i = 0; i < inputBlockSize; i++) {
                    bufferFloat[i] = (float) bufferByte[i] / (float) Byte.MAX_VALUE;
                }
            } else {
                throw new RuntimeException("Unrecognized Encoding format only ENCODING_PCM_FLOAT," +
                        " ENCODING_PCM_16BIT , ENCODING_PCM_8BIT is supported");
            }

            if(unitsRead < 0) {
                Log.e(TAG, "Audio read failed: error " + unitsRead);
                running = false;
                break;
            }

            if(!paused) {
                readDone(bufferFloat);
            }
        }

        if(audioInput.getState() == AudioRecord.RECORDSTATE_RECORDING)
            audioInput.stop();
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
    @Override
    public int getSampleRate() {
        return sampleRate;
    }

    /**
     * Set the sample rate for the input
     *
     * @param sampleRate sample rate to set
     */
    @Override
    public void setSampleRate(int sampleRate) throws Exception {
        boolean running = isRunning();

        if(running) {
            stop();
        }

        this.sampleRate = sampleRate;

        initialise();
        if(running) {
            start();
        }
    }

    /**
     * @return current input block size
     */
    protected int getInputBlockSize() {
        return inputBlockSize;
    }

    /**
     * Set the block size for the audio input. The audio stream will be restarted if running.
     * If the block size is set lower than is possible by the device. The minimum block size is used
     *
     * @param inputBlockSize block size to set to
     */
    void setInputBlockSize(int inputBlockSize) {
        boolean running = isRunning();

        if(running) {
            stop();
        }

        this.inputBlockSize = inputBlockSize;

        initialise();
        if(running) {
            start();
        }
    }
}
