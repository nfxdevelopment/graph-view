package com.nfx.android.graph.androidgraph;

import com.nfx.android.graph.androidgraph.AxisScale.AxisParameters;

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
     * @param axisParameters  parameters of graph shown
     */
    LinGridLines(AxisOrientation axisOrientation, AxisParameters axisParameters) {
        super(axisOrientation, axisParameters);
        childGridLineScale = Scale.linear;
    }

    /**
     * Gives the intersect point of a grid line a when the zoom level is 100 percent
     *
     * @param gridLine grid line to find out the intersecting value
     * @return intersecting point
     */
    @Override
    float intersect(int gridLine) {
        if(gridLine >= numberOfGridLines || gridLine < 0) {
            return GRID_LINE_OUT_OF_RANGE;
        }

        float intersect = gridLinesOffset + getGridLineDrawableWidth() * (float) gridLine;

        intersect -= getFixedZoomDisplay().getDisplayOffsetPercentage();
        intersect /= getFixedZoomDisplay().getZoomLevelPercentage();

        return intersect;
    }
}
