package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

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
    protected GridLines mGridLines;
    /**
     * paint for the text
     */
    protected Paint mTextPaint = new Paint();
    /**
     * context of application
     */
    private Context mContext;
    /**
     * text size before scaling for screen has been applied
     */
    private int mUnscaledTextSize = 16;
    /**
     * the lowest number that the axis displays
     */
    private float mMinimumAxisValue = 0;
    /**
     * the highest number the axis displays
     */
    private float mMaximumAxisValue = 0;
    /**
     * the span of the values displayed on the axis
     */
    private float mAxisValueSpan = 1;

    /**
     * Constructor
     *
     * @param context   application context is used for dimension reasons
     * @param gridLines grid lines axis is related to
     * @param minimumAxisValue the lowest number that the axis displays
     * @param maximumAxisValue the highest number the axis displays
     */
    AxisText(Context context, GridLines gridLines, float minimumAxisValue, float maximumAxisValue) {
        mContext = context;
        mGridLines = gridLines;

        // Check to ensure the minimum is less that the maximum
        if (maximumAxisValue > minimumAxisValue) {
            mMinimumAxisValue = minimumAxisValue;
            mMaximumAxisValue = maximumAxisValue;
            mAxisValueSpan = maximumAxisValue - minimumAxisValue;
        } else {
            Log.w(TAG, "Given maximum value is less than minimum value");
        }

        float textScale = mContext.getResources().getDisplayMetrics().density;

        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setTextSize((float) mUnscaledTextSize * textScale);
        mTextPaint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
    }

    /**
     * calculates the string to display for a given grid number
     *
     * @param lineNumber line number to calculate for
     * @return a string which represents the value of the grid line
     */
    protected String displayString(int lineNumber) {
        // +1 as the first grid line is not 0 i.e not the minimum, this would be the boarder along
        // with the maximum, which is on the boarder
        float percentageAcrossScale =
                (float) (lineNumber + 1) / (float) (mGridLines.getNumberOfGridLines() + 1);
        float valueWithoutOffset = mAxisValueSpan * percentageAcrossScale;
        float valueToDisplay = valueWithoutOffset + mMinimumAxisValue;
        return String.valueOf(valueToDisplay);
    }

    /**
     * This uses the maximum number which will be displayed on the axis, it still may be wider than
     * this though change in the future TODO
     *
     * @return maximum width of text
     */
    public float getMaximumTextWidth() {
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(String.valueOf(mMaximumAxisValue), 0,
                String.valueOf(mMaximumAxisValue).length(), bounds);
        return bounds.width();
    }

    /**
     * Returns the size height of axis text, including the stroke width of the text. This is not
     * the case in the sdk implementations
     *
     * @return text height
     */
    public float getRealTextHeight() {
        return (Math.abs(mTextPaint.ascent()) + Math.abs(mTextPaint.descent()));
    }
}
