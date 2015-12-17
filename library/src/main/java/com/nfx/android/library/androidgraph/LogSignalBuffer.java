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

        float logMaximumZoomLevel = (float) (Math.pow(10 * mAxisSpanValue,
                ZoomDisplay.MAXIMUM_ZOOM_LEVEL) - 1f);

        float linearSpacing = mXZoomDisplay.getZoomLevelPercentage() / (numberOfPoints - 1f);

        synchronized(this) {
            for(int i = 0; i < numberOfPoints; i++) {
                float linearStartPos = linearSpacing * i;
                float pointOffsetPercentage =
                        (float) (Math.pow(10 * mAxisSpanValue, (
                                mXZoomDisplay.getDisplayOffsetPercentage()
                                        + linearStartPos)) - 1f) / logMaximumZoomLevel;
                float pointOffset = pointOffsetPercentage * (float) (getSizeOfBuffer() - 1);
                float arrayPosRemainder = pointOffset % 1;

                if(arrayPosRemainder == 0) {
                    scaledBuffer[i] = (mBuffer[(int) pointOffset]
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
    }
}
