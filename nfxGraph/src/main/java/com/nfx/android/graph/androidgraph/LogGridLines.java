package com.nfx.android.graph.androidgraph;

import com.nfx.android.graph.androidgraph.AxisScale.AxisParameters;

/**
 * NFX Development
 * Created by nick on 28/10/15.
 *
 * To be extend by a axis specific class.
 */
abstract class LogGridLines extends GridLines {
    /**
     * minimum value displayed by these grid lines
     */
    private final float mGridLineMinimumValue;
    /**
     * span of these gridlines
     */
    private final float mGridLineSpanValue;

    /**
     * Constructor
     *
     * @param axisOrientation       either the x or y axis
     * @param axisParameters        parameters of graph shown
     * @param gridLineMinimumValue  minimum value displayed by these grid lines
     * @param gridLineMaximumValue  maximum value displayed by these grid lines
     */
    LogGridLines(AxisOrientation axisOrientation, AxisParameters axisParameters,
                 float gridLineMinimumValue, float gridLineMaximumValue) {
        super(axisOrientation, axisParameters);
        this.childGridLineScale = Scale.logarithmic;
        this.mGridLineMinimumValue = gridLineMinimumValue;
        this.mGridLineSpanValue = gridLineMaximumValue - gridLineMinimumValue;
    }

    @Override
    float intersect(int gridLine) {
        if(gridLine >= numberOfGridLines || gridLine < 0) {
            return GRID_LINE_OUT_OF_RANGE;
        }

        return axisParameters.scaledAxisToGraphPosition(mGridLineMinimumValue + (
                (mGridLineSpanValue /
                (float) (getNumberOfGridLines() - 1)) * (float) gridLine));
    }
}
