package com.nfx.android.library.androidgraph;

/**
 * NFX Development
 * Created by nick on 28/10/15.
 *
 * To be extend by a axis specific class.
 */
public abstract class LinGridLines extends GridLines {
    /**
     * Contructor which passes straight through
     *
     * @param axisOrientation either the x or y axis
     */
    public LinGridLines(AxisOrientation
            axisOrientation) {
        super(axisOrientation);
    }


    /**
     * Gives the value of where a grid line will interest x on the screen
     * @param gridLine        grid line to find, base 0
     * @param dimensionLength Either width or height
     * @return the x Intersect or -1 if the grid line is out of range or grid Line should not be
     *          shown
     */
    public float intersectZoomCompensated(int gridLine, int dimensionLength) {
        float intersect = intersect(gridLine);
        if (intersect == -1) {
            return -1;
        }

        float virtualIntersectPercentage = intersect / dimensionLength;

        if (virtualIntersectPercentage > mZoomDisplay.getDisplayOffsetPercentage() &&
                virtualIntersectPercentage < mZoomDisplay.getZoomLevelPercentage() +
                        mZoomDisplay.getDisplayOffsetPercentage()) {

            float intersectPercentage =
                    (virtualIntersectPercentage - mZoomDisplay.getDisplayOffsetPercentage()) /
                            mZoomDisplay.getZoomLevelPercentage();
            // are we in range
            float drawLocation = dimensionLength * intersectPercentage;
            return drawLocation;
        } else {
            // It is outside our desired viewable area
            return -1f;
        }
    }
}
