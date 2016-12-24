package com.nfx.android.graph.androidgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.nfx.android.graph.androidgraph.AxisScale.GraphParameters;

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
    private final Rect bounds = new Rect();
    /**
     * Graph Limits
     */
    private final GraphParameters graphParameters;
    /**
     * The zoom levels from the x and y Axis
     */
    private ZoomDisplay xZoomDisplay;
    private ZoomDisplay yZoomDisplay;
    /**
     * The values to display in each corner
     */
    private String leftX;
    private String rightX;
    private String topY;
    private String bottomY;

    /**
     * @param context  needed to work out the text size
     * @param graphParameters An object holding the graph limits
     */
    BoarderText(Context context, GraphParameters graphParameters) {

        // Set a default zoom Display
        xZoomDisplay = new ZoomDisplay(1f, 0f);
        yZoomDisplay = new ZoomDisplay(1f, 0f);

        this.graphParameters = graphParameters;

        float textScale = context.getResources().getDisplayMetrics().density;

        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setColor(Color.GRAY);
        /*
      text size before scaling for screen has been applied
     */
        int mUnscaledTextSize = 16;
        paint.setTextSize((float) mUnscaledTextSize * textScale);
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        String sMaximumString = "00.00K";
        paint.getTextBounds(sMaximumString, 0, sMaximumString.length(), bounds);

        calculateValuesToDisplay();
    }

    @Override
    public void doDraw(Canvas canvas) {
        canvas.drawText(topY, getDrawableArea().getLeft() + bounds.width(),
                getDrawableArea().getTop() + bounds.height(), paint);

        canvas.drawText(bottomY, getDrawableArea().getLeft() + bounds.width(),
                getDrawableArea().getBottom() - getRealTextHeight(), paint);

        canvas.drawText(leftX, (getDrawableArea().getLeft() + bounds.width() + (bounds.width()
                        / 2)),
                getDrawableArea().getBottom() - Math.abs(paint.descent()), paint);

        canvas.drawText(rightX, getDrawableArea().getRight(),
                getDrawableArea().getBottom() - Math.abs(paint.descent()), paint);
    }

    /**
     * We calculate the value ahead of time to removeAllChildGridLines any hold up in doDraw
     */
    void calculateValuesToDisplay() {
        topY = displayString(graphParameters.getYAxisParameters().getMinimumValue() +
                (yZoomDisplay.getFarSideOffsetPercentage() * graphParameters.getYAxisParameters
                        ().getAxisSpan()));
        bottomY = displayString(graphParameters.getYAxisParameters().getMinimumValue() +
                (yZoomDisplay.getDisplayOffsetPercentage() * graphParameters.getYAxisParameters
                        ().getAxisSpan()));
        leftX = displayString(graphParameters.getXAxisParameters().graphPositionToScaledAxis(
                xZoomDisplay.getDisplayOffsetPercentage()));
        rightX = displayString(graphParameters.getXAxisParameters().graphPositionToScaledAxis(
                xZoomDisplay.getFarSideOffsetPercentage()));
    }

    /**
     * Return a string for the given value to display. Truncation will be applied to allow the
     * string
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
        yZoomDisplay = zoomDisplay;

        /*
            We have to list for changes to the zoom offset to update the shown values
         */
        yZoomDisplay.addListener(new ZoomChangedListener() {
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
        xZoomDisplay = zoomDisplay;

        /*
            We have to list for changes to the zoom offset to update the shown values
         */
        xZoomDisplay.addListener(new ZoomChangedListener() {
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
        return (Math.abs(paint.ascent()) + Math.abs(paint.descent()));
    }
}
