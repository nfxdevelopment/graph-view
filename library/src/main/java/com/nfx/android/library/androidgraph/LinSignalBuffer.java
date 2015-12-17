package com.nfx.android.library.androidgraph;

/**
 * NFX Development
 * Created by nick on 06/12/15.
 */
public class LinSignalBuffer extends SignalBuffer {

    /**
     * Constructor
     *
     * @param id           unique id for signal
     * @param sizeOfBuffer size expecting to receive
     */
    public LinSignalBuffer(int id, int sizeOfBuffer) {
        super(id, sizeOfBuffer);
    }

    /**
     * This will return a buffer with the desired {@code numberOfPoints} size. Essentially scaling
     * scaling the read buffer
     * The algorithm works out each new point separately. More often than not the new sample will
     * fall between to of buffer points therefore we have to do some aliasing
     *
     * @param scaledBuffer buffer to fill
     */
    public void getScaledBuffer(float[] scaledBuffer) {

        int numberOfPoints = scaledBuffer.length;

        synchronized(this) {
            // -1 to get the spacing between the samples.
            float spacing = mXZoomDisplay.getZoomLevelPercentage() / (numberOfPoints - 1);

            for(int i = 0; i < numberOfPoints; i++) {
                float pointOffsetPercentage = (spacing * (float) i)
                        + mXZoomDisplay.getDisplayOffsetPercentage();
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
