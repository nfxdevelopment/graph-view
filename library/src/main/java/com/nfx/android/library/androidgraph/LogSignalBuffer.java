package com.nfx.android.library.androidgraph;

/**
 * NFX Development
 * Created by nick on 06/12/15.
 */
public class LogSignalBuffer extends SignalBuffer {
    /**
     * Given span for the X axis. This is used to calculate the logarithmic scale
     */
    private float mAxisSpanValue;

    /**
     * Constructor
     *
     * @param id           unique id for signal
     * @param sizeOfBuffer size expecting to receive
     */
    public LogSignalBuffer(int id, int sizeOfBuffer, float axisSpanValue) {
        super(id, sizeOfBuffer);
        mAxisSpanValue = axisSpanValue;
    }

    /**
     * This will return a buffer with the desired {@code numberOfPoints} size. It will
     * logarithmically scale the buffer so the receiving buffer can plot in a linear fashion
     * The algorithm works out each new point separately. More often than not the new sample will
     * fall between to of buffer points therefore we have to do some aliasing
     *
     * @param scaledBuffer buffer to fill
     */
    public void getScaledBuffer(float[] scaledBuffer) {

        int numberOfPoints = scaledBuffer.length;

        synchronized(this) {
            for(int i = 0; i < numberOfPoints; i++) {
                // Calculate the array index to read from
                float centreOffset = scaledIndexToLogBufferIndex(i, numberOfPoints);
                float averageFromLowerIndex = scaledIndexToLogBufferIndex(i - 1, numberOfPoints);
                float averageFromHigherIndex = scaledIndexToLogBufferIndex(i + 1, numberOfPoints);

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
     * calculates where the scaled buffer index should point in relation to the given log buffer
     *
     * @param index               desired scaled index to calculate
     * @param desiredBufferLength length od the desired scaled buffer
     * @return
     */
    private float scaledIndexToLogBufferIndex(int index, int desiredBufferLength) {
        float logMaximumZoomLevel = (float) (Math.pow(10 * mAxisSpanValue,
                ZoomDisplay.MAXIMUM_ZOOM_LEVEL) - 1f);

        float linearSpacing = mXZoomDisplay.getZoomLevelPercentage() / (desiredBufferLength - 1f);

        float linearStartPos = linearSpacing * index;
        float pointOffsetPercentage =
                (float) (Math.pow(10 * mAxisSpanValue, (
                        mXZoomDisplay.getDisplayOffsetPercentage()
                                + linearStartPos)) - 1f) / logMaximumZoomLevel;
        return pointOffsetPercentage * (float) (getSizeOfBuffer() - 1);
    }
}
