package com.nfx.android.library.androidgraph;

import android.util.Log;

/**
 * NFX Development
 * Created by nick on 31/10/15.
 * <p/>
 * A signal buffer holds a buffer with additional information on how it should be displayed on
 * screen. This can be used to pass buffer information between graphical and input objects.
 */
public class SignalBuffer {
    private static final String TAG = "SignalBuffer";
    /**
     * Information about the scaling of signal in the y axis
     */
    private ZoomDisplay mYZoomDisplay;
    /**
     * Information about the scaling of signal in the y axis
     */
    private ZoomDisplay mXZoomDisplay;
    /**
     * So we now if the data is logarithmic or linear
     */
    private SignalScale mSignalScale;

    /**
     * Unique Id for signal
     */
    private int mId;

    /**
     * buffer of given size which is worked out at runtime. This data is normalized 0-1
     */
    private float[] mBuffer;

    /**
     * Constructor
     *
     * @param id           unique id for signal
     * @param sizeOfBuffer size expecting to receive
     * @param signalScale  linear or logarithmic
     */
    public SignalBuffer(int id, int sizeOfBuffer, SignalScale signalScale) {
        mId = id;
        mBuffer = new float[sizeOfBuffer];
        mSignalScale = signalScale;

        mXZoomDisplay = new ZoomDisplay(1f, 0f);
        mYZoomDisplay = new ZoomDisplay(1f, 0f);
    }

    /**
     * Sets the member buffer. Please ensure data is normalised to 0-1 before setting. If the
     * buffer passed in does not match the size of the member buffer. It will not be set and a
     * Log warning is displayed.
     *
     * @param buffer source for buffer copy
     */
    public void setBuffer(float[] buffer) {
        synchronized (this) {
            if (mBuffer.length == buffer.length) {
                System.arraycopy(buffer, 0, mBuffer, 0, mBuffer.length);
            } else {
                Log.w(TAG, "Buffer passed " + buffer.length +
                        " in does not match size of signal buffer " + mBuffer.length);
            }
        }
    }

    /**
     * This will return a buffer with the desired {@code numberOfPoints} size. Essentially scaling
     * scaling the read buffer
     * The algorithm works out each new point separately. More often than not the new sample will
     * fall between to of buffer points therefore we have to do some aliasing
     * TODO Seems to be a bugin this where lowerPosition is greater than buffer length
     *
     * @param numberOfPoints desired number of points for new buffer
     * @return new buffer
     */
    public float[] getScaledBuffer(int numberOfPoints) {

        float[] scaledBuffer = new float[numberOfPoints];

        synchronized (this) {
            // -1 to get the spacing between the samples.
            float spacing = mXZoomDisplay.getZoomLevelPercentage() / (numberOfPoints - 1);

            for (int i = 0; i < numberOfPoints; i++) {
                float pointOffsetPercentage = (spacing * (float) i)
                        + mXZoomDisplay.getDisplayOffsetPercentage();
                float pointOffset = pointOffsetPercentage * (float) (getSizeOfBuffer() - 1);
                float arrayPosRemainder = pointOffset % 1;

                if (arrayPosRemainder == 0) {
                    scaledBuffer[i] = ( mBuffer[(int) pointOffset]
                            - mYZoomDisplay.getDisplayOffsetPercentage())
                                        / mYZoomDisplay.getZoomLevelPercentage();
                } else {
                    int lowerPosition = (int) Math.floor(pointOffset);
                    int upperPosition = (int) Math.ceil(pointOffset);

                    float lowerValue = mBuffer[lowerPosition];
                    float upperValue = mBuffer[upperPosition];

                    scaledBuffer[i] = (lowerValue + ((upperValue - lowerValue) * arrayPosRemainder)
                            - mYZoomDisplay.getDisplayOffsetPercentage())
                                        / mYZoomDisplay.getZoomLevelPercentage();
                }
            }
        }

        return scaledBuffer;
    }

    public int getId() {
        return mId;
    }

    public int getSizeOfBuffer() {
        return mBuffer.length;
    }

    public ZoomDisplay getXZoomDisplay() {
        return mXZoomDisplay;
    }

    public ZoomDisplay getYZoomDisplay() {
        return mYZoomDisplay;
    }

    public SignalScale getSignalScale() {
        return mSignalScale;
    }

    public enum SignalScale {
        logarithmic,
        linear
    }
}
