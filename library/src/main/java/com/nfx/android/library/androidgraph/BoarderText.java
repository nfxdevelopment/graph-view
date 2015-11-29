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
 * As the boarder is draw separately to the axis is separated. This allows us to display a always
 * up to date value on the boarders
 */
public class BoarderText extends DrawableObject {
    /**
     * paint for the text
     */
    private final Paint mTextPaint = new Paint();
    /**
     * Minimum and the span of the x and y axis
     */
    private final float mXMinimum;
    private final float mYMinimum;
    private final float mXSpan;
    private final float mYSpan;
    /**
     * The format in which to display on screen
     */
    private final DecimalFormat mDecimalFormat = new DecimalFormat("0.00");
    /**
     * Maximum Bounds of text
     */
    private final Rect mBounds = new Rect();
    /**
     * The zoom levels from the x and y Axis
     */
    private ZoomDisplay mXZoomDisplay;
    private ZoomDisplay mYZoomDisplay;
    /**
     * The values to display in each corner
     */
    private String mLeftX;
    private String mRightX;
    private String mTopY;
    private String mBottomY;

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
        /*
      text size before scaling for screen has been applied
     */
        int mUnscaledTextSize = 16;
        mTextPaint.setTextSize((float) mUnscaledTextSize * textScale);
        mTextPaint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        mDecimalFormat.setRoundingMode(RoundingMode.CEILING);

        String sMaximumString = "-0.00";
        mTextPaint.getTextBounds(sMaximumString, 0, sMaximumString.length(), mBounds);

        calculateValuesToDisplay();
    }

    @Override
    public void doDraw(Canvas canvas) {
        canvas.drawText(mTopY, getDrawableArea().getLeft() + mBounds.width(),
                getDrawableArea().getTop() + mBounds.height(), mTextPaint);

        canvas.drawText(mBottomY, getDrawableArea().getLeft() + mBounds.width(),
                getDrawableArea().getBottom() - getRealTextHeight(), mTextPaint);

        canvas.drawText(mLeftX, 2 * (getDrawableArea().getLeft() + mBounds.width()),
                getDrawableArea().getBottom() - Math.abs(mTextPaint.descent()), mTextPaint);

        canvas.drawText(mRightX, getDrawableArea().getRight(),
                getDrawableArea().getBottom() - Math.abs(mTextPaint.descent()), mTextPaint);
    }

    /**
     * We calculate the value ahead of time to stop any hold up in doDraw
     */
    private void calculateValuesToDisplay() {
        mTopY = String.valueOf(mDecimalFormat.format(mYMinimum +
                ((1 - mYZoomDisplay.getDisplayOffsetPercentage()) * mYSpan)));
        mBottomY = String.valueOf(mDecimalFormat.format(mYMinimum +
                ((1 - mYZoomDisplay.getFarSideOffsetPercentage()) * mYSpan)));
        mLeftX = String.valueOf(mDecimalFormat.format(mXMinimum +
                (mXZoomDisplay.getDisplayOffsetPercentage() * mXSpan)));
        mRightX = String.valueOf(mDecimalFormat.format(mXMinimum +
                (mXZoomDisplay.getFarSideOffsetPercentage() * mXSpan)));
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

        /*
            We have to list for changes to the zoom offset to update the shown values
         */
        mXZoomDisplay.setTheListener(new ZoomDisplay.ZoomChangedListener() {
            @Override
            public void zoomChanged() {
                calculateValuesToDisplay();
            }
        });
        mYZoomDisplay.setTheListener(new ZoomDisplay.ZoomChangedListener() {
            @Override
            public void zoomChanged() {
                calculateValuesToDisplay();
            }
        });
    }

    /**
     * Returns the size height of axis text, including the stroke width of the text. This is not
     * the case in the sdk implementations
     *
     * @return text height
     */
    private float getRealTextHeight() {
        return (Math.abs(mTextPaint.ascent()) + Math.abs(mTextPaint.descent()));
    }
}
