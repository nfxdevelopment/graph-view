package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.nfx.android.library.androidgraph.AxisScale.GraphParameters;

/**
 * NFX Development
 * Created by nick on 24/11/15.
 * <p/>
 * As the boarder is draw separately to the axis is separated. This allows us to display a always
 * up to date value on the boarders
 */
class BoarderText extends DrawableObject {
    /**
     * Maximum Bounds of text
     */
    private final Rect mBounds = new Rect();
    /**
     * Graph Limits
     */
    private final GraphParameters mGraphParameters;
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
     * @param graphParameters An object holding the graph limits
     */
    BoarderText(Context context, GraphParameters graphParameters) {

        // Set a default zoom Display
        mXZoomDisplay = new ZoomDisplay(1f, 0f);
        mYZoomDisplay = new ZoomDisplay(1f, 0f);

        this.mGraphParameters = graphParameters;

        float textScale = context.getResources().getDisplayMetrics().density;

        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.RIGHT);
        mPaint.setColor(Color.GRAY);
        /*
      text size before scaling for screen has been applied
     */
        int mUnscaledTextSize = 16;
        mPaint.setTextSize((float) mUnscaledTextSize * textScale);
        mPaint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        String sMaximumString = "00.00K";
        mPaint.getTextBounds(sMaximumString, 0, sMaximumString.length(), mBounds);

        calculateValuesToDisplay();
    }

    @Override
    public void doDraw(Canvas canvas) {
        canvas.drawText(mTopY, getDrawableArea().getLeft() + mBounds.width(),
                getDrawableArea().getTop() + mBounds.height(), mPaint);

        canvas.drawText(mBottomY, getDrawableArea().getLeft() + mBounds.width(),
                getDrawableArea().getBottom() - getRealTextHeight(), mPaint);

        canvas.drawText(mLeftX, (getDrawableArea().getLeft() + mBounds.width() + (mBounds.width()
                        / 2)),
                getDrawableArea().getBottom() - Math.abs(mPaint.descent()), mPaint);

        canvas.drawText(mRightX, getDrawableArea().getRight(),
                getDrawableArea().getBottom() - Math.abs(mPaint.descent()), mPaint);
    }

    /**
     * We calculate the value ahead of time to removeAllChildGridLines any hold up in doDraw
     */
    private void calculateValuesToDisplay() {
        mTopY = displayString(mGraphParameters.getYAxisParameters().getMinimumValue() +
                (mYZoomDisplay.getFarSideOffsetPercentage() * mGraphParameters.getYAxisParameters
                        ().getAxisSpan()));
        mBottomY = displayString(mGraphParameters.getYAxisParameters().getMinimumValue() +
                (mYZoomDisplay.getDisplayOffsetPercentage() * mGraphParameters.getYAxisParameters
                        ().getAxisSpan()));
        mLeftX = displayString(mGraphParameters.getXAxisParameters().graphPositionToScaledAxis(
                mXZoomDisplay.getDisplayOffsetPercentage()));
        mRightX = displayString(mGraphParameters.getXAxisParameters().graphPositionToScaledAxis(
                mXZoomDisplay.getFarSideOffsetPercentage()));
    }

    /**
     * Return a string for the given value to display. Trucation will be applied to allow the string
     * to be displayed correctly on screen
     *
     * @param valueToDisplay the number to display
     * @return display string
     */
    private String displayString(float valueToDisplay) {
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
     * @param zoomDisplay the y Zoom levels
     */
    void setYZoomDisplay(ZoomDisplay zoomDisplay) {
        mYZoomDisplay = zoomDisplay;

        /*
            We have to list for changes to the zoom offset to update the shown values
         */
        mYZoomDisplay.addListener(new ZoomChangedListener() {
            @Override
            public void zoomChanged() {
                calculateValuesToDisplay();
            }
        });
    }

    /**
     * When the user moves the signal around we need to know. This allows us to display a always
     * up to date value on the boards
     *
     * @param zoomDisplay the y Zoom levels
     */
    void setXZoomDisplay(ZoomDisplay zoomDisplay) {
        mXZoomDisplay = zoomDisplay;

        /*
            We have to list for changes to the zoom offset to update the shown values
         */
        mXZoomDisplay.addListener(new ZoomChangedListener() {
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
        return (Math.abs(mPaint.ascent()) + Math.abs(mPaint.descent()));
    }
}
