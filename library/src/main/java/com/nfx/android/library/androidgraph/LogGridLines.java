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
    double maxLogValue;
    /**
     * Given span for the axis. This is used to calculate the logarithmic scale
     */
    private float mAxisSpanValue;
    /**
     * The Decade the grid lines represent
     */
    private float mDecade;

    /**
     * Constructor which passes straight through
     *]
     * @param axisOrientation either the x or y axis
     */
    LogGridLines(AxisOrientation axisOrientation, float axisSpanValue, float decade) {
        super(axisOrientation);
        mAxisSpanValue = axisSpanValue;
        mDecade = decade;
    }

    /**
     * Ensures maxLogValue is always up to date when we use it. This super has to be called before
     * any drawing is done
     *
     * @param canvas a canvas to draw onto
     */
    @Override
    public void doDraw(Canvas canvas) {
    }

    float intersect(int gridLine) {
        if(gridLine >= mNumberOfGridLines || gridLine < 0) {
            return GRID_LINE_OUT_OF_RANGE;
        }

        float lineLog = GraphManager.log(mDecade * (1f / (float) (getNumberOfGridLines() - 1) *
                (float) gridLine));
        float maxLog = GraphManager.log(mAxisSpanValue);

        return lineLog / maxLog;
    }
}
