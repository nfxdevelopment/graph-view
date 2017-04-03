package com.nfx.android.graph.androidgraph;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.nfx.android.graph.androidgraph.AxisScale.AxisParameters;

/**
 * NFX Development
 * Created by nick on 29/10/15.
 * <p/>
 * Base class for drawing axis text onto a canvas
 */
abstract class AxisText extends DrawableObject {
    /**
     * grid lines that text is relating to
     */
    final GridLines gridLines;
    /**
     * Store the values in a array as working the value every draw was memory intensive
     */
    String[] gridLineValues;
    /**
     * The Bounds of the display text
     */
    final Rect bounds = new Rect();
    /**
     * graph scale limits
     */
    private final AxisParameters axisParameters;

    /**
     * Constructor
     *
     * @param context   application context is used for dimension reasons
     * @param gridLines grid lines axis is related to
     * @param axisParameters the lowest number that the axis displays
     */
    AxisText(Context context, GridLines gridLines, AxisParameters axisParameters) {
        this.gridLines = gridLines;
        this.axisParameters = axisParameters;

        float textScale = context.getResources().getDisplayMetrics().density;

        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.GRAY);
        /*
          text size before scaling for screen has been applied
         */
        int mUnscaledTextSize = 16;
        paint.setTextSize((float) mUnscaledTextSize * textScale);
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        gridLineValues = new String[gridLines.getNumberOfGridLines()];
        calculateGridLineValues();

        String sMaximumString = "00.00K";
        paint.getTextBounds(sMaximumString, 0, sMaximumString.length(), bounds);
    }

    /**
     * Return the string to display for the given grid line
     *
     * @param gridLine grid line number between 0 and max number of grid lines
     * @return string to display
     */
    private String displayString(int gridLine) {
        float locationOnGraph = gridLines.intersect(gridLine);

        float valueToDisplay = axisParameters.graphPositionToScaledAxis(locationOnGraph);
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

    /**
     * This uses the maximum number which will be displayed on the axis
     *
     * @return maximum width of text
     */
    float getMaximumTextWidth() {
        return bounds.width();
    }

    /**
     * Returns the size height of axis text, including the stroke width of the text. This is not
     * the case in the sdk implementations
     *
     * @return text height
     */
    float getRealTextHeight() {
        return (Math.abs(paint.ascent()) + Math.abs(paint.descent()));
    }

    /**
     * Calculate and completed the array of displayed strings. Call when there is a change in graph
     * display
     */
    void calculateGridLineValues() {
        for(int i = 0; i < gridLineValues.length; ++i) {
            gridLineValues[i] = displayString(i);
        }
    }
}
