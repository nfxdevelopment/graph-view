package com.nfx.android.library.androidgraph;

/**
 * NFX Development
 * Created by nick on 28/10/15.
 *
 * To be extend by a axis specific class.
 */
abstract class LinGridLines extends GridLines {
    /**
     * Constructor which passes straight through
     *
     * @param axisOrientation either the x or y axis
     */
    LinGridLines(AxisOrientation axisOrientation) {
        super(axisOrientation);
        mChildGridLineScale = GraphManager.Scale.linear;
    }

    /**
     * Gives the intersect point of a grid line a when the zoom level is 100 percent
     *
     * @param gridLine grid line to find out the intersecting value
     * @return intersecting point
     */
    @Override
    float intersect(int gridLine) {
        if(gridLine >= mNumberOfGridLines || gridLine < 0) {
            return GRID_LINE_OUT_OF_RANGE;
        }

        return mGridLinesOffset + getGridLineDrawableWidth() * (float) gridLine;
    }
}
