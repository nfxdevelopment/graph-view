package com.nfx.android.library.androidgraph;

import com.nfx.android.library.androidgraph.AxisScale.AxisParameters;

/**
 * NFX Development
 * Created by nick on 28/10/15.
 *
 * To be extend by a axis specific class.
 */
abstract class LogGridLines extends GridLines {
    private final float mGridLineMinimumValue;
    private final float mGridLineSpanValue;

    /**
     * Constructor which passes straight through
     *]
     * @param axisOrientation either the x or y axis
     */
    LogGridLines(AxisOrientation axisOrientation, AxisParameters axisParameters,
                 float gridLineMinimumValue, float gridLineMaximumValue) {
        super(axisOrientation, axisParameters);
        this.mChildGridLineScale = Scale.logarithmic;
        this.mGridLineMinimumValue = gridLineMinimumValue;
        this.mGridLineSpanValue = gridLineMaximumValue - gridLineMinimumValue;
    }

    @Override
    float intersect(int gridLine) {
        if(gridLine >= mNumberOfGridLines || gridLine < 0) {
            return GRID_LINE_OUT_OF_RANGE;
        }

        return mAxisParameters.scaledAxisToGraphPosition(mGridLineMinimumValue + (
                (mGridLineSpanValue /
                (float) (getNumberOfGridLines() - 1)) * (float) gridLine));
    }
}
