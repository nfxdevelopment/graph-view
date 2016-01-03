package com.nfx.android.library.androidgraph;

/**
 * NFX Development
 * Created by nick on 28/10/15.
 *
 * To be extend by a axis specific class.
 */
abstract class LogGridLines extends GridLines {
    /**
     * Given span for the axis. This is used to calculate the logarithmic scale
     */
    private final float mAxisSpanValue;

    /**
     * Constructor which passes straight through
     *]
     * @param axisOrientation either the x or y axis
     */
    LogGridLines(AxisOrientation axisOrientation, float axisSpanValue) {
        super(axisOrientation);
        mAxisSpanValue = axisSpanValue;
    }

    @Override
    float intersect(int gridLine) {
        if(gridLine >= mNumberOfGridLines || gridLine < 0) {
            return GRID_LINE_OUT_OF_RANGE;
        }

        float lineLog = GraphManager.logFrequency(
                ((1f / (float) (getNumberOfGridLines())) * (float) gridLine));
        float maxLog = GraphManager.logFrequency(mAxisSpanValue);

        return mGridLinesOffset + (lineLog / maxLog);
    }
}
