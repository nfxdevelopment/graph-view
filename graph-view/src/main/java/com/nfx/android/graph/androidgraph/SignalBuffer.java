package com.nfx.android.graph.androidgraph;

import android.util.Log;

import com.nfx.android.graph.androidgraph.AxisScale.AxisParameters;
import com.nfx.android.graph.graphbufferinput.InputListener;

/**
 * NFX Development
 * Created by nick on 31/10/15.
 * <p>
 * A signal buffer holds a buffer with additional information on how it should be displayed on
 * screen. This can be used to pass buffer information between graphical and input objects.
 */
public class SignalBuffer extends InputListener implements SignalBufferInterface {
    private static final String TAG = "SignalBuffer";
    /**
     * Information about the scaling of signal in the y axis
     */
    private final ZoomDisplay yZoomDisplay;
    /**
     * The Parameters of the axis'
     */
    private final AxisParameters xAxisParameters;
    /**
     * buffer of given size which is worked out at runtime. This data is normalized 0-1
     */
    private float[] buffer;
    /**
     * Constructor
     *
     * @param sizeOfBuffer size expecting to receive
     * @param xAxisParameters parameters of x axis
     */
    @SuppressWarnings("WeakerAccess")
    public SignalBuffer(int sizeOfBuffer, AxisParameters xAxisParameters, ZoomDisplay zoomDisplay) {
        this.xAxisParameters = xAxisParameters;

        buffer = new float[sizeOfBuffer];

        yZoomDisplay = zoomDisplay;
    }

    /**
     * This will return a buffer with the desired {@code numberOfPoints} size. It will
     * logarithmically scale (if required)  the buffer so the receiving buffer can plot in a linear
     * fashion The algorithm works out each new point separately. More often than not the new sample
     * will fall between to of buffer points therefore we have to do some aliasing
     *
     * @param minimumValuesBuffer minimum values of the block form
     * @param maximumValuesBuffer maximum values of the block form
     * @param maximumXValue minimum value of scaled buffer
     * @param minimumXValue maximum value of scaled buffer
     * @param scaleToParameters target axis
     * */
    @Override
    public void getScaledMinimumMaximumBuffers(float[] minimumValuesBuffer, float[]
            maximumValuesBuffer,
                                        float minimumXValue, float maximumXValue,
                                        AxisParameters scaleToParameters) {
        if(minimumValuesBuffer.length != maximumValuesBuffer.length) {
            Log.e(TAG, "Block Form buffers are not of equal length");
        }

        int numberOfPoints = minimumValuesBuffer.length;

        synchronized(this) {
            for(int i = 0; i < numberOfPoints; i++) {
                // Calculate the array index to read from
                float centreOffset = scaledIndexBufferIndex(i, minimumXValue, maximumXValue,
                        scaleToParameters, numberOfPoints);
                float averageFromLowerIndex = scaledIndexBufferIndex(i - 1, minimumXValue,
                        maximumXValue, scaleToParameters,
                        numberOfPoints);
                float averageFromHigherIndex = scaledIndexBufferIndex(i + 1, minimumXValue,
                        maximumXValue, scaleToParameters,
                        numberOfPoints);

                // Work out the point falling between the centre and next offset and also the centre
                // and last offset
                float lowBound = centreOffset + ((averageFromLowerIndex - centreOffset) / 2f);
                float highBound = centreOffset + ((averageFromHigherIndex - centreOffset) / 2f);

                // If the point falls between 2 array indexes and the band is displaying less than
                // two array points, then calculate the gradient for the crossing point at the
                // current position. This smooths out the lower frequencies
                if(centreOffset - lowBound < 2f && highBound - centreOffset < 2f) {
                    float arrayPosRemainder = centreOffset % 1;

                    if(arrayPosRemainder == 0) {
                        maximumValuesBuffer[i] = minimumValuesBuffer[i] =
                                (buffer[(int) centreOffset]
                                        - yZoomDisplay.getDisplayOffsetPercentage())
                                        / yZoomDisplay.getZoomLevelPercentage();
                    } else {
                        int lowerPosition = (int) Math.floor(centreOffset);
                        int upperPosition = (int) Math.ceil(centreOffset);

                        float lowerValue = buffer[lowerPosition];
                        float upperValue = buffer[upperPosition];

                        maximumValuesBuffer[i] = minimumValuesBuffer[i] =
                                (lowerValue + ((upperValue - lowerValue) * arrayPosRemainder)
                                        - yZoomDisplay.getDisplayOffsetPercentage())
                                        / yZoomDisplay.getZoomLevelPercentage();
                    }
                } else { // If we are displaying more than 2 frequencies at a given point then
                    // display the average
                    int roundedLowBound = Math.round(lowBound);
                    int roundedHighBound = Math.round(highBound);
                    if(roundedLowBound < 0) {
                        roundedLowBound = 0;
                    }
                    if(roundedHighBound > (buffer.length - 1)) {
                        roundedHighBound = (buffer.length - 1);
                    }

                    // Get the minimum value
                    float displayValue = minimumValueForGivenRange(roundedLowBound,
                            roundedHighBound);

                    minimumValuesBuffer[i] = (displayValue - yZoomDisplay
                            .getDisplayOffsetPercentage())
                            / yZoomDisplay.getZoomLevelPercentage();

                    // Get the maximum value
                    displayValue = maximumValueForGivenRange(roundedLowBound, roundedHighBound);

                    maximumValuesBuffer[i] = (displayValue - yZoomDisplay
                            .getDisplayOffsetPercentage())
                            / yZoomDisplay.getZoomLevelPercentage();

                }
            }
        }

    }

    /**
     * This will return a buffer with the desired {@code numberOfPoints} size. It will
     * logarithmically scale (if required)  the buffer so the receiving buffer can plot in a linear
     * fashion The algorithm works out each new point separately. More often than not the new sample
     * will fall between to of buffer points therefore we have to do some aliasing
     *
     * @param scaledBuffer buffer to fill
     * @param scaleToParameters  fill buffer with values within these parameters
     */
    void getScaledBuffer(float[] scaledBuffer, float minimumValue, float maximumValue,
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
                        scaledBuffer[i] = (buffer[(int) centreOffset]
                                - yZoomDisplay.getDisplayOffsetPercentage())
                                / yZoomDisplay.getZoomLevelPercentage();
                    } else {
                        int lowerPosition = (int) Math.floor(centreOffset);
                        int upperPosition = (int) Math.ceil(centreOffset);

                        float lowerValue = buffer[lowerPosition];
                        float upperValue = buffer[upperPosition];

                        scaledBuffer[i] = (lowerValue + ((upperValue - lowerValue) *
                                arrayPosRemainder)
                                - yZoomDisplay.getDisplayOffsetPercentage())
                                / yZoomDisplay.getZoomLevelPercentage();
                    }
                } else { // If we are displaying more than 2 frequencies at a given point then
                    // display the average
                    int roundedLowBound = Math.round(lowBound);
                    int roundedHighBound = Math.round(highBound);
                    if(roundedLowBound < 0) {
                        roundedLowBound = 0;
                    }
                    if(roundedHighBound > (buffer.length - 1)) {
                        roundedHighBound = (buffer.length - 1);
                    }

                    float displayValue = maximumValueForGivenRange(roundedLowBound,
                            roundedHighBound);

                    scaledBuffer[i] = (displayValue - yZoomDisplay.getDisplayOffsetPercentage())
                            / yZoomDisplay.getZoomLevelPercentage();

                }
            }
        }
    }

    /**
     * Return the raw buffer
     *
     * @return float array with raw information
     */
    @Override
    public float[] getUnscaledBuffer() {
        return buffer;
    }


    /**
     * This will return a value for a given position in the buffer.
     *
     * @param scalePosition value between mMinimumX and mMaximumX
     * @return the value at given position
     */
    public float getValueAtPosition(float scalePosition) {
        if(scalePosition < xAxisParameters.getMinimumValue() ||
                scalePosition > xAxisParameters.getMaximumValue()) {
            return 0;
        }
        // Determine position in the buffer
        float percentageOffset = (scalePosition - xAxisParameters.getMinimumValue()) /
                xAxisParameters.getAxisSpan();

        synchronized(this) {
            float bufferIndexToRead = percentageOffset * (float) (getSizeOfBuffer() - 1);

            float arrayPosRemainder = bufferIndexToRead % 1;

            if(arrayPosRemainder == 0) {
                return (buffer[(int) bufferIndexToRead]
                        - yZoomDisplay.getDisplayOffsetPercentage())
                        / yZoomDisplay.getZoomLevelPercentage();
            } else {
                int lowerPosition = (int) Math.floor(bufferIndexToRead);
                int upperPosition = (int) Math.ceil(bufferIndexToRead);

                float lowerValue = buffer[lowerPosition];
                float upperValue = buffer[upperPosition];

                return (lowerValue + ((upperValue - lowerValue) *
                        arrayPosRemainder)
                        - yZoomDisplay.getDisplayOffsetPercentage())
                        / yZoomDisplay.getZoomLevelPercentage();
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

        float bufferPercentagePosition = (frequencyToRead - xAxisParameters.getMinimumValue()) /
                xAxisParameters.getAxisSpan();

        return bufferPercentagePosition * (getSizeOfBuffer() - 1);
    }

    /**
     * Average value for a given range within buffer
     *
     * @param minimumArrayPosition first position in array
     * @param maxArrayPosition     last position in array
     * @return average value
     */
    @SuppressWarnings("unused")
    private float averageValueForGivenRange(int minimumArrayPosition, int maxArrayPosition) {
        float displayValue = 0;
        int positionDifference = minimumArrayPosition - maxArrayPosition + 1;

        for(int g = minimumArrayPosition; g <= maxArrayPosition; ++g) {
            displayValue += buffer[g];
        }

        displayValue /= (float) positionDifference;

        return displayValue;
    }

    /**
     * Find the maximum value for a given range within buffer
     *
     * @param minimumArrayPosition first position in array
     * @param maxArrayPosition     last position in array
     * @return maximum value
     */
    private float maximumValueForGivenRange(int minimumArrayPosition, int maxArrayPosition) {
        float displayValue = 0;

        for(int g = minimumArrayPosition; g <= maxArrayPosition; ++g) {
            if(buffer[g] > displayValue) {
                displayValue = buffer[g];
            }
        }

        return displayValue;
    }

    @Override
    public void inputBlockSizeUpdate(int blockSize) {
        synchronized(this) {
            buffer = new float[blockSize];
        }
    }

    /**
     * Sets the member buffer. Please ensure data is normalised to 0-1 before setting. If the
     * buffer passed in does not match the size of the member buffer. It will not be set and a
     * Log warning is displayed.
     *
     * @param buffer source for buffer copy
     */
    @Override
    public void bufferUpdate(float[] buffer) {
        synchronized(this) {
            if(this.buffer.length == buffer.length) {
                System.arraycopy(buffer, 0, this.buffer, 0, this.buffer.length);
            } else {
                Log.e(TAG, "Buffer passed " + buffer.length +
                        " in does not match size of signal buffer " + this.buffer.length);
            }
        }
    }

    @Override
    public void inputRemoved() {
        // TODO Look at a way to automate the removal of a signal
    }

    /**
     * Find the minimum value for a given range within buffer
     *
     * @param minimumArrayPosition first position in array
     * @param maxArrayPosition     last position in array
     * @return maximum value
     */
    private float minimumValueForGivenRange(int minimumArrayPosition, int maxArrayPosition) {
        float displayValue = 1;

        for(int g = minimumArrayPosition; g <= maxArrayPosition; ++g) {
            if(buffer[g] < displayValue) {
                displayValue = buffer[g];
            }
        }

        return displayValue;
    }

    private int getSizeOfBuffer() {
        return buffer.length;
    }

    @Override
    public ZoomDisplay getYZoomDisplay() {
        return yZoomDisplay;
    }

    @Override
    public AxisParameters getXAxisParameters() {
        return xAxisParameters;
    }
}
