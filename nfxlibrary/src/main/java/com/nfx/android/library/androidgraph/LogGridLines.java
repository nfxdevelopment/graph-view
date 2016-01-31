package com.nfx.android.library.androidgraph;

/**
 * NFX Development
 * Created by nick on 28/10/15.
 *
 * To be extend by a axis specific class.
 */
abstract class LogGridLines extends GridLines {

    private final float mGridLineMinimumValue;
    private final float mGridLineSpanValue;
    private final float mLogAxisSpanValue;


    /**
     * Constructor which passes straight through
     *]
     * @param axisOrientation either the x or y axis
     */
    LogGridLines(AxisOrientation axisOrientation, float gridLineMinimumValue,
                 float gridLineMaximumValue, float axisSpanValue) {
        super(axisOrientation);
        mChildGridLineScale = GraphManager.Scale.logarithmic;
        this.mGridLineMinimumValue = gridLineMinimumValue;
        this.mGridLineSpanValue = gridLineMaximumValue - gridLineMinimumValue;
        this.mLogAxisSpanValue = GraphManager.frequencyToGraphPosition(axisSpanValue);
    }

    @Override
    float intersect(int gridLine) {
        if(gridLine >= mNumberOfGridLines || gridLine < 0) {
            return GRID_LINE_OUT_OF_RANGE;
        }

        float lineLog = GraphManager.frequencyToGraphPosition(mGridLineMinimumValue +
                ((mGridLineSpanValue / (float) (getNumberOfGridLines() - 1)) * (float) gridLine));

        return lineLog / mLogAxisSpanValue;
    }
}
