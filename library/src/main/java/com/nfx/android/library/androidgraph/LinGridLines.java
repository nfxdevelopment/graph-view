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
     * @param zoomDisplay     zoom information for the orientation
     * @param axisOrientation either the x or y axis
     */
    public LinGridLines(ZoomDisplay zoomDisplay, AxisOrientation
            axisOrientation) {
        super(zoomDisplay, axisOrientation);
    }

    /**
     * Gives the value of where a grid line will interest x on the screen
     * @param gridLine        grid line to find, base 0
     * @param dimensionLength Either width or height
     * @return the x Intersect or -1 if the grid line is out of range or grid Line should not be
     *          shown
     */
    public float intersect(int gridLine, int dimensionLength) {
        if (gridLine >= mNumberOfGridLines || gridLine < 0) {
            return -1f;
        }

        // +1 as there would be mNumberOfGridLines intersecting the graph which splits
        // mNumberOfGridLines + 1 areas
        float spacing = ((float) dimensionLength) / (float) (mNumberOfGridLines + 1);
        // The first line lies at on the area boundary of the first block hence +1
        // This is the value where it would intersect if zoom was at 100%
        float virtualIntersect = spacing * (float) (gridLine + 1);
        float virtualIntersectPercentage = virtualIntersect / (float) dimensionLength;

        if (virtualIntersectPercentage > mZoomDisplay.getDisplayOffsetPercentage() &&
                virtualIntersectPercentage < mZoomDisplay.getZoomLevelPercentage() +
                        mZoomDisplay.getDisplayOffsetPercentage()) {

            float intersectPercentage =
                    (virtualIntersectPercentage - mZoomDisplay.getDisplayOffsetPercentage()) /
                            mZoomDisplay.getZoomLevelPercentage();

            return dimensionLength * intersectPercentage;
        } else {
            // It is outside our desired viewable area
            return -1f;
        }
    }
}
