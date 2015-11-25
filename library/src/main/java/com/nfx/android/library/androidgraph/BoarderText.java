package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * NFX Development
 * Created by nick on 24/11/15.
 * <p/>
 * As the boarder is draw separately to the axis is separated. This alows us to display a always
 * up to date value on the boarders
 */
public class BoarderText extends DrawableObject {
    /**
     * paint for the text
     */
    protected Paint mTextPaint = new Paint();
    /**
     * Minimum and the span of the x and y axis
     */
    private float mXMinimum;
    private float mYMinimum;
    private float mXSpan;
    private float mYSpan;
    /**
     * Max width of string to display
     */
    private String sMaximumString = "-0.00";
    /**
     * text size before scaling for screen has been applied
     */
    private int mUnscaledTextSize = 16;

    /**
     * The zoom levels from the x and y Axis
     */
    private ZoomDisplay mXZoomDisplay;
    private ZoomDisplay mYZoomDisplay;

    /**
     * @param context  needed to work out the text size
     * @param xMinimum minimum value on the x axis
     * @param xMaximum maximum value on the x axis
     * @param yMinimum minimum value on the y axis
     * @param yMaximum maximum value on the y axis
     */
    public BoarderText(Context context, float xMinimum, float xMaximum, float yMinimum,
                       float yMaximum) {

        // Set a default zoom Display
        mXZoomDisplay = new ZoomDisplay(1f, 0f);
        mYZoomDisplay = new ZoomDisplay(1f, 0f);

        mXMinimum = xMinimum;
        mYMinimum = yMinimum;

        mXSpan = xMaximum - xMinimum;
        mYSpan = yMaximum - yMinimum;

        float textScale = context.getResources().getDisplayMetrics().density;

        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.RIGHT);
        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setTextSize((float) mUnscaledTextSize * textScale);
        mTextPaint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
    }

    @Override
    public void doDraw(Canvas canvas) {
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.CEILING);

        Rect bounds = new Rect();
        mTextPaint.getTextBounds(sMaximumString, 0, sMaximumString.length(), bounds);

        String displayString = String.valueOf(df.format(mYMinimum +
                (mYZoomDisplay.getFarSideOffsetPercentage() * mYSpan)));
        canvas.drawText(displayString, getDrawableArea().getLeft() + bounds.width(),
                getDrawableArea().getTop() + bounds.height(), mTextPaint);

        displayString = String.valueOf(df.format(mYMinimum +
                (mYZoomDisplay.getDisplayOffsetPercentage() * mYSpan)));
        canvas.drawText(displayString, getDrawableArea().getLeft() + bounds.width(),
                getDrawableArea().getBottom() - getRealTextHeight(), mTextPaint);

        displayString = String.valueOf(df.format(mXMinimum +
                (mXZoomDisplay.getDisplayOffsetPercentage() * mXSpan)));
        canvas.drawText(displayString, 2 * (getDrawableArea().getLeft() + bounds.width()),
                getDrawableArea().getBottom() - Math.abs(mTextPaint.descent()), mTextPaint);

        displayString = String.valueOf(df.format(mXMinimum +
                (mXZoomDisplay.getFarSideOffsetPercentage() * mXSpan)));
        canvas.drawText(displayString, getDrawableArea().getRight(),
                getDrawableArea().getBottom() - Math.abs(mTextPaint.descent()), mTextPaint);
    }

    /**
     * The Board text can be drawn anywhere. Therefore no limits are reported
     *
     * @param currentDrawableArea the drawable area canvas to calculate the area taken
     */
    @Override
    protected void calculateRemainingDrawableArea(DrawableArea currentDrawableArea) {

    }

    /**
     * When the user moves the signal around we need to know. This allows us to display a always
     * up to date value on the boards
     *
     * @param xZoomDisplay the x Zoom levels
     * @param yZoomDisplay the y Zoom levels
     */
    public void setZoomDisplay(ZoomDisplay xZoomDisplay, ZoomDisplay yZoomDisplay) {
        mXZoomDisplay = xZoomDisplay;
        mYZoomDisplay = yZoomDisplay;
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
