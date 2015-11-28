package com.nfx.android.library.androidgraph;

/**
 * NFX Development
 * Created by nick on 28/10/15.
 *
 * To be extend by a axis specific class.
 */
public abstract class LinGridLines extends GridLines {
    /**
     * Constructor which passes straight through
     *
     * @param axisOrientation either the x or y axis
     */
    LinGridLines(AxisOrientation
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
    protected float intersectZoomCompensated(int gridLine, int dimensionLength) {
        float intersect = intersect(gridLine);
        if (intersect == GRID_LINE_OUT_OF_RANGE) {
            return GRID_LINE_OUT_OF_RANGE;
        }

        float virtualIntersectPercentage = intersect / dimensionLength;

        if (virtualIntersectPercentage < mZoomDisplay.getDisplayOffsetPercentage()) {
            return LESS_THAN_VIEWABLE_AREA;
        } else if (virtualIntersectPercentage > mZoomDisplay.getFarSideOffsetPercentage()) {
            return GREATER_THAN_VIEWABLE_AREA;
        } else {
            float intersectPercentage =
                    (virtualIntersectPercentage - mZoomDisplay.getDisplayOffsetPercentage()) /
                            mZoomDisplay.getZoomLevelPercentage();

            return dimensionLength * intersectPercentage;
        }
    }
}
