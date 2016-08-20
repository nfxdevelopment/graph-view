package com.nfx.android.library.androidgraph;

import android.util.Log;

import com.nfx.android.library.androidgraph.AxisScale.AxisParameters;

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
    private final ZoomDisplay mYZoomDisplay;
    /**
     * The minimum value the buffer holds on the X scale
     */
    private final AxisParameters mXAxisParameters;
    /**
     * buffer of given size which is worked out at runtime. This data is normalized 0-1
     */
    private float[] mBuffer;
    /**
     * Constructor
     *
     * @param sizeOfBuffer size expecting to receive
     */
    @SuppressWarnings("WeakerAccess")
    public SignalBuffer(int sizeOfBuffer, AxisParameters xAxisParameters) {
        this.mXAxisParameters = xAxisParameters;

        mBuffer = new float[sizeOfBuffer];

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
                Log.e(TAG, "Buffer passed " + buffer.length +
                        " in does not match size of signal buffer " + mBuffer.length);
            }
        }
    }

    /**
     * This will return a buffer with the desired {@code numberOfPoints} size. It will
     * logarithmically scale the buffer so the receiving buffer can plot in a linear fashion
     * The algorithm works out each new point separately. More often than not the new sample will
     * fall between to of buffer points therefore we have to do some aliasing
     *
     * @param scaledBuffer buffer to fill
     * @param scaleToParameters  fill buffer with values within these parameters
     */
    public void getScaledBuffer(float[] scaledBuffer, float minimumValue, float maximumValue,
                                AxisParameters scaleToParameters) {

        int numberOfPoints = scaledBuffer.length;

        synchronized(this) {
            for(int i = 0; i < numberOfPoints; i++) {
                // Calculate the array index to read from
                float centreOffset = scaledIndexBufferIndex(i, minimumValue, maximumValue,
                        scaleToParameters, numberOfPoints);
                float averageFromLowerIndex = scaledIndexBufferIndex(i - 1, minimumValue,
                        maximumValue, scaleToParameters,
                        numberOfPoints);
                float averageFromHigherIndex = scaledIndexBufferIndex(i + 1, minimumValue,
                        maximumValue, scaleToParameters,
                        numberOfPoints);

                // Work out the point falling between the centre and next offset and also the centre
                // and last offset
                float lowBound = centreOffset + ((averageFromLowerIndex - centreOffset) / 2f);
                float highBound = centreOffset + ((averageFromHigherIndex - centreOffset) / 2f);

                float ceilLowBound = (float) Math.ceil(lowBound);
                float ceilHighBound = (float) Math.ceil(highBound);
                float ceilCentre = (float) Math.ceil(centreOffset);

                // If the point falls between 2 array indexes and the band is displaying less than
                // two array points, then calculate the gradient for the crossing point at the
                // current position. This smooths out the lower frequencies
                if(ceilCentre - ceilLowBound < 2f && ceilHighBound - ceilCentre < 2f) {
                    float arrayPosRemainder = centreOffset % 1;

                    if(arrayPosRemainder == 0) {
                        scaledBuffer[i] = (mBuffer[(int) centreOffset]
                                - mYZoomDisplay.getDisplayOffsetPercentage())
                                / mYZoomDisplay.getZoomLevelPercentage();
                    } else {
                        int lowerPosition = (int) Math.floor(centreOffset);
                        int upperPosition = (int) Math.ceil(centreOffset);

                        float lowerValue = mBuffer[lowerPosition];
                        float upperValue = mBuffer[upperPosition];

                        scaledBuffer[i] = (lowerValue + ((upperValue - lowerValue) *
                                arrayPosRemainder)
                                - mYZoomDisplay.getDisplayOffsetPercentage())
                                / mYZoomDisplay.getZoomLevelPercentage();
                    }
                } else { // If we are displaying more than 2 frequencies at a given point then
                    // display the average
                    int roundedLowBound = Math.round(lowBound);
                    int roundedHighBound = Math.round(highBound);
                    if(roundedLowBound < 0) {
                        roundedLowBound = 0;
                    }
                    if(roundedHighBound > (mBuffer.length - 1)) {
                        roundedHighBound = (mBuffer.length - 1);
                    }

                    int roundedDifference = roundedHighBound - roundedLowBound + 1;

                    float average = 0;
                    for(int g = roundedLowBound; g <= roundedHighBound; ++g) {
                        average += mBuffer[g];
                    }

                    average /= (float) roundedDifference;

                    scaledBuffer[i] = (average - mYZoomDisplay.getDisplayOffsetPercentage())
                            / mYZoomDisplay.getZoomLevelPercentage();

                }
            }
        }
    }

    /**
     * This will return a value for a given position in the buffer.
     *
     * @param scalePosition value between mMinimumX and mMaximumX
     * @return the value at given position
     */
    public float getValueAtPosition(float scalePosition) {
        if(scalePosition < mXAxisParameters.getMinimumValue() ||
                scalePosition > mXAxisParameters.getMaximumValue()) {
            return 0;
        }
        // Determine position in the buffer
        float percentageOffset = (scalePosition - mXAxisParameters.getMinimumValue()) /
                mXAxisParameters.getAxisSpan();

        synchronized(this) {
            float bufferIndexToRead = percentageOffset * (float) (getSizeOfBuffer() - 1);

            float arrayPosRemainder = bufferIndexToRead % 1;

            if(arrayPosRemainder == 0) {
                return (mBuffer[(int) bufferIndexToRead]
                        - mYZoomDisplay.getDisplayOffsetPercentage())
                        / mYZoomDisplay.getZoomLevelPercentage();
            } else {
                int lowerPosition = (int) Math.floor(bufferIndexToRead);
                int upperPosition = (int) Math.ceil(bufferIndexToRead);

                float lowerValue = mBuffer[lowerPosition];
                float upperValue = mBuffer[upperPosition];

                return (lowerValue + ((upperValue - lowerValue) *
                        arrayPosRemainder)
                        - mYZoomDisplay.getDisplayOffsetPercentage())
                        / mYZoomDisplay.getZoomLevelPercentage();
            }
        }
    }

    /**
     * calculates where the scaled buffer index should point in relation to the given
     * log Frequency buffer
     *
     * @param index          desired scaled index to calculate
     * @param scaleToParameters scaled buffer limits
     * @param numberOfPoints number of points in the scaled buffer
     * @return read buffer index to use
     */
    private float scaledIndexBufferIndex(int index, float minimumValue, float maximumValue,
                                         AxisParameters scaleToParameters, int numberOfPoints) {
        float minimumGraphPosition = (minimumValue - scaleToParameters.getMinimumValue()) /
                scaleToParameters.getAxisSpan();
        float maximumGraphPosition = (maximumValue - scaleToParameters.getMinimumValue()) /
                scaleToParameters.getAxisSpan();
        float graphPositionSpan = maximumGraphPosition - minimumGraphPosition;


        float frequencyToRead = scaleToParameters.graphPositionToScaledAxis(
                minimumGraphPosition + (((float) index / (float) numberOfPoints) *
                        graphPositionSpan));

        float bufferPercentagePosition = (frequencyToRead - mXAxisParameters.getMinimumValue()) /
                mXAxisParameters.getAxisSpan();

        return bufferPercentagePosition * (getSizeOfBuffer() - 1);
    }

    public int getSizeOfBuffer() {
        return mBuffer.length;
    }

    public void setSizeOfBuffer(int sizeOfBuffer) {
        synchronized(this) {
            mBuffer = new float[sizeOfBuffer];
        }
    }

    public ZoomDisplay getYZoomDisplay() {
        return mYZoomDisplay;
    }

    public AxisParameters getXAxisParameters() {
        return mXAxisParameters;
    }
}
