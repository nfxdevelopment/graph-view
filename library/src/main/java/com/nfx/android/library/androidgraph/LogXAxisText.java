package com.nfx.android.library.androidgraph;

import android.content.Context;

/**
 * NFX Development
 * Created by nick on 22/12/15.
 */
public class LogXAxisText extends XAxisText {
    /**
     * Constructor
     *
     * @param context          application context is used for dimension reasons
     * @param gridLines        grid lines axis is related to
     * @param minimumAxisValue the lowest number that the axis displays
     * @param maximumAxisValue the highest number the axis displays
     */
    LogXAxisText(Context context, GridLines gridLines, float minimumAxisValue, float
            maximumAxisValue) {
        super(context, gridLines, minimumAxisValue, maximumAxisValue);
    }

    @Override
    String displayString(int gridLine) {
        float locationOnGraph = mGridLines.intersect(gridLine);

        float valueToDisplay =
                (float) Math.pow(mAxisValueSpan, locationOnGraph);

        float nonNegativeValue = Math.abs(valueToDisplay);

        if(nonNegativeValue < 10f) {
            return String.valueOf((float) Math.round(valueToDisplay * 100d) / 100d);
        } else if(nonNegativeValue < 100f) {
            return String.valueOf((float) Math.round(valueToDisplay * 10d) / 10d);
        } else if(nonNegativeValue < 1000f) {
            return String.valueOf(Math.round(valueToDisplay));
        } else if(nonNegativeValue < 100000f) {
            return String.valueOf((float) Math.round(valueToDisplay / 10d) / 100f) + "K";
        } else {
            return "NaN";
        }
    }
}
