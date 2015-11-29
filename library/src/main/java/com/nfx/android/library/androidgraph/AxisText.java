package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * NFX Development
 * Created by nick on 29/10/15.
 *
 * Base class for drawing axis text onto a canvas
 */
public abstract class AxisText extends DrawableObject {
    private static final String TAG = "AxisText";

    /**
     * grid lines that text is relating to
     */
    final GridLines mGridLines;
    /**
     * paint for the text
     */
    final Paint mTextPaint = new Paint();
    /**
     * Store the values in a array as working the value every draw was memory intensive
     */
    final String[] mGridLineValues;
    /**
     * The Bounds of the display text
     */
    final Rect mBounds = new Rect();
    /**
     * The format to display in
     */
    private final DecimalFormat mDecimalFormat = new DecimalFormat("0.00");
    /**
     * boarder size
     */
    int mGraphBoarderSize = 0;
    /**
     * the lowest number that the axis displays
     */
    private float mMinimumAxisValue = 0;
    /**
     * the span of the values displayed on the axis
     */
    private float mAxisValueSpan = 1;
    /**
     * the highest number the axis displays
     */
    private float mMaximumAxisValue = 0;

    /**
     * Constructor
     *
     * @param context   application context is used for dimension reasons
     * @param gridLines grid lines axis is related to
     * @param minimumAxisValue the lowest number that the axis displays
     * @param maximumAxisValue the highest number the axis displays
     */
    AxisText(Context context, GridLines gridLines, float minimumAxisValue, float maximumAxisValue) {
        mGridLines = gridLines;

        // Check to ensure the minimum is less that the maximum
        if (maximumAxisValue > minimumAxisValue) {
            mMinimumAxisValue = minimumAxisValue;
            mMaximumAxisValue = maximumAxisValue;
            mAxisValueSpan = maximumAxisValue - minimumAxisValue;
        } else {
            Log.w(TAG, "Given maximum value is less than minimum value");
        }

        float textScale = context.getResources().getDisplayMetrics().density;

        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.GRAY);
        /*
          text size before scaling for screen has been applied
         */
        int mUnscaledTextSize = 16;
        mTextPaint.setTextSize((float) mUnscaledTextSize * textScale);
        mTextPaint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        mDecimalFormat.setRoundingMode(RoundingMode.CEILING);

        mGridLineValues = new String[gridLines.getNumberOfGridLines()];
        calculateGridLineValues();

        String sMaximumString = "-0.00";
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(sMaximumString, 0, sMaximumString.length(), bounds);
    }

    private String displayString(int gridLine) {
        float locationOnGraph = locationOnGraph(gridLine);
        // +1 as we are not labeling the limits here
        float valueToDisplay = mMinimumAxisValue + (mAxisValueSpan * locationOnGraph);

        return String.valueOf(mDecimalFormat.format(valueToDisplay));
    }

    abstract float locationOnGraph(int gridLine);

    /**
     * This uses the maximum number which will be displayed on the axis, it still may be wider than
     * this though change in the future TODO
     *
     * @return maximum width of text
     */
    float getMaximumTextWidth() {
        Rect bounds = new Rect();
        /*
      Max width of string to display
     */
        String sMaximumString = "-0.00";
        mTextPaint.getTextBounds(sMaximumString, 0, sMaximumString.length(), bounds);
        return bounds.width();
    }

    /**
     * Returns the size height of axis text, including the stroke width of the text. This is not
     * the case in the sdk implementations
     *
     * @return text height
     */
    float getRealTextHeight() {
        return (Math.abs(mTextPaint.ascent()) + Math.abs(mTextPaint.descent()));
    }

    public void setGraphBoarderSize(int boarderSize) {
        mGraphBoarderSize = boarderSize;
    }

    public float getMinimumAxisValue() {
        return mMinimumAxisValue;
    }

    public float getMaximumAxisValue() {
        return mMaximumAxisValue;
    }

    public void calculateGridLineValues() {
        for (int i = 0; i < mGridLineValues.length; ++i) {
            mGridLineValues[i] = displayString(i);
        }
    }
}
