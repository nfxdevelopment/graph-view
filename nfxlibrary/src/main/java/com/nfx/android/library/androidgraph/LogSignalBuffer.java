package com.nfx.android.library.androidgraph;

/**
 * NFX Development
 * Created by nick on 06/12/15.
 */
public class LogSignalBuffer extends SignalBuffer {
    /**
     * 2 to power of the maximum value
     */
    private final float mLogMaximumZoomLevel;

    /**
     * Constructor
     *
     * @param sizeOfBuffer size expecting to receive
     */
    public LogSignalBuffer(int sizeOfBuffer, float axisSpanValue) {
        super(sizeOfBuffer, axisSpanValue);
        mLogMaximumZoomLevel = GraphManager.graphPositionToFrequency(
                axisSpanValue, ZoomDisplay.MAXIMUM_ZOOM_LEVEL);
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
        float linearSpacing = mXZoomDisplay.getZoomLevelPercentage() / (float) (numberOfPoints - 1);


        synchronized(this) {
            for(int i = 0; i < numberOfPoints; i++) {
                // Calculate the array index to read from
                float centreOffset = scaledIndexToLogBufferIndex(i, linearSpacing);
                float averageFromLowerIndex = scaledIndexToLogBufferIndex(i - 1, linearSpacing);
                float averageFromHigherIndex = scaledIndexToLogBufferIndex(i + 1, linearSpacing);

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
     * calculates where the scaled buffer index should point in relation to the given
     * log Frequency buffer
     *
     * @param index               desired scaled index to calculate
     * @param linearSpacing       Space between each point on a linear scale
     * @return read buffer index to use
     */
    private float scaledIndexToLogBufferIndex(int index, float linearSpacing) {
        float linearStartPos = linearSpacing * index;

        float pointOffsetPercentage =
                (GraphManager.graphPositionToFrequency(mAxisSpanValue, (
                        mXZoomDisplay.getDisplayOffsetPercentage()
                                + linearStartPos))) / mLogMaximumZoomLevel;
        return pointOffsetPercentage * (float) (getSizeOfBuffer() - 1);
    }
}
