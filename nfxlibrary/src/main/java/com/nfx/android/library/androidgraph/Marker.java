package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * NFX Development
 * Created by nick on 25/10/15.
 *
 * Draws a marker onto a graph
 */
public class Marker extends DrawableObject {
    /**
     * Color of the grid lines
     */
    private static final int INITIAL_LINE_COLOR = Color.RED;
    /**
     * Use an even number to ensure all grid line strokes look the same
     */
    private static final float INITIAL_LINE_STROKE_WIDTH = 4f;
    private final GraphView.GraphSignalInputInterface mGraphInterface;
    private final MarkerUpdateInterface mMarkerUpdateInterface;
    private final int mSignalId;
    /**
     *
     */
    private float mCircleRadius = 20f;
    private SignalBufferInterface mSignalInterface;
    private float mMarkerPosition = 0.5f;

    public Marker(int signalId, GraphView.GraphSignalInputInterface graphSignalInputInterface,
                  SignalBufferInterface signal, MarkerUpdateInterface markerUpdateInterface) {
        this.mSignalId = signalId;
        this.mGraphInterface = graphSignalInputInterface;
        this.mSignalInterface = signal;
        this.mMarkerUpdateInterface = markerUpdateInterface;
        mPaint.setColor(INITIAL_LINE_COLOR);
        mPaint.setStrokeWidth(INITIAL_LINE_STROKE_WIDTH);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mMarkerUpdateInterface.markerColour(INITIAL_LINE_COLOR);
    }

    @Override
    public void doDraw(Canvas canvas) {
        float xValue = mGraphInterface.getGraphParameters().getXAxisParameters().
                graphPositionToScaledAxis(mMarkerPosition);
        float yValue = mSignalInterface.getValueAtPosition(xValue);
        mMarkerUpdateInterface.markerPositionUpdate(xValue, yValue);

        if(mMarkerPosition > mGraphInterface.getGraphXZoomDisplay().getDisplayOffsetPercentage() &&
                mMarkerPosition < mGraphInterface.getGraphXZoomDisplay()
                        .getFarSideOffsetPercentage()) {

            float centreX = getMarkerPositionInPx();
            float centreY = getDrawableArea().getTop() +
                    (1f - mSignalInterface.getValueAtPosition(xValue)) *
                            getDrawableArea().getHeight();

            canvas.drawCircle(
                    centreX,
                    centreY,
                    mCircleRadius,
                    mPaint
                    );
            canvas.drawLine(centreX - mCircleRadius,
                    centreY,
                    centreX + mCircleRadius,
                    centreY,
                    mPaint
            );
            canvas.drawLine(centreX,
                    centreY - mCircleRadius,
                    centreX,
                    centreY + mCircleRadius,
                    mPaint
            );
        }
    }

    @Override
    public void surfaceChanged(DrawableArea drawableArea) {
        float dimensionDivisor = 30f;
        if(drawableArea.getHeight() < drawableArea.getWidth()) {
            mCircleRadius = drawableArea.getHeight() / dimensionDivisor;
        } else {
            mCircleRadius = drawableArea.getWidth() / 30;
        }
        super.surfaceChanged(drawableArea);
    }

    @Override
    public void calculateRemainingDrawableArea(DrawableArea currentDrawableArea) {

    }

    public float getMarkerPosition() {
        return mMarkerPosition;
    }

    public void setMarkerPosition(float markerPosition) {
        if(markerPosition > 1.0f) {
            this.mMarkerPosition = 1.0f;
        } else if(markerPosition < 0.0f) {
            this.mMarkerPosition = 0.0f;
        } else {
            this.mMarkerPosition = markerPosition;
        }
    }

    public float getMarkerPositionInPx() {
        float intersect = mMarkerPosition;

        intersect -= mGraphInterface.getGraphXZoomDisplay().getDisplayOffsetPercentage();
        intersect /= mGraphInterface.getGraphXZoomDisplay().getZoomLevelPercentage();

        intersect *= getDrawableArea().getWidth();
        intersect += getDrawableArea().getLeft();

        return intersect;
    }

    @SuppressWarnings("unused")
    public void setMarkerPositionInPx(float markerPositionInPx) {
        float markerPosition = markerPositionInPx;
        markerPosition -= getDrawableArea().getLeft();
        markerPosition /= getDrawableArea().getWidth();

        markerPosition += mGraphInterface.getGraphXZoomDisplay().getDisplayOffsetPercentage();
        markerPosition *= mGraphInterface.getGraphXZoomDisplay().getZoomLevelPercentage();

        setMarkerPosition(markerPosition);
    }

    @Override
    public void setColour(int colour) {
        super.setColour(colour);
        mMarkerUpdateInterface.markerColour(colour);
    }

    public void setSignalInterface(SignalBufferInterface mSignalInterface) {
        this.mSignalInterface = mSignalInterface;
    }

    public GraphView.GraphSignalInputInterface getGraphInterface() {
        return mGraphInterface;
    }

    public int getSignalId() {
        return mSignalId;
    }

    public interface MarkerUpdateInterface {
        void markerColour(int color);

        void markerPositionUpdate(float xValue, float yValue);
    }

}
