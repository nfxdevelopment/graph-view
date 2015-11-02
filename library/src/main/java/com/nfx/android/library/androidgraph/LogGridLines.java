package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;

/**
 * NFX Development
 * Created by nick on 28/10/15.
 *
 * To be extend by a axis specific class.
 */
public abstract class LogGridLines extends GridLines {
    /**
     * Used to work out positions relative to this value
     */
    protected double maxLogValue;

    /**
     * Contructor which passes straight through
     *]
     * @param axisOrientation either the x or y axis
     */
    public LogGridLines(AxisOrientation
            axisOrientation) {
        super(axisOrientation);
    }

    /**
     * Ensures maxLogValue is always up to date when we use it. This super has to be called before
     * any drawing is done
     *
     * @param canvas a canvas to draw onto
     */
    @Override
    public void doDraw(Canvas canvas) {
        maxLogValue = Math.log(getDrawableArea().getHeight());
    }

    /**
     * Gives the value of where a grid line will interest x on the screen
     * @param gridLine        grid line to find, base 0
     * @param dimensionLength Either the width or length
     * @return the x Intersect or -1 if the grid line is out of range
     */
    @Override
    public float intersect(int gridLine, int dimensionLength) {
        if (gridLine >= mNumberOfGridLines || gridLine < 0) {
            return -1f;
        }

        // +1 as there would be mNumberOfGridLines intersecting the graph which splits
        // mNumberOfGridLines + 1 areas
        float spacing = (float) dimensionLength / (float) (mNumberOfGridLines + 1);
        // The first line lies at on the area boundary of the first block hence +1
        float linearOffset = spacing * (float) (gridLine + 1);

        float virtualIntersect =
                (float) (Math.log(linearOffset) / maxLogValue) * (float) dimensionLength;

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
