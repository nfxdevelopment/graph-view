package com.nfx.android.graph.androidgraph;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * NFX Development
 * Created by nick on 25/10/15.
 * <p/>
 * Draws a marker onto a graph
 */
public class Marker extends DrawableObject {
    /**
     * Default colour of the marker
     */
    private static final int INITIAL_LINE_COLOR = Color.RED;
    /**
     * Default stroke width of the lines
     */
    private static final float INITIAL_LINE_STROKE_WIDTH = 4f;
    /**
     * GraphView interface
     */
    private final GraphView.GraphSignalInputInterface mGraphInterface;
    /**
     * Post updates to this object
     */
    private final MarkerUpdateInterface mMarkerUpdateInterface;
    /**
     * Id of the signal
     */
    private final int mSignalId;
    /**
     * interface to signal
     */
    private SignalBufferInterface mSignalInterface;
    /**
     * Size of the circle
     */
    private float mCircleRadius = 20f;
    /**
     * Default position for the marker
     */
    private float mMarkerPosition = (float) Math.random();

    /**
     * @param signalId                  Id of the signal the markers are attached to
     * @param graphSignalInputInterface interface to the graph view
     * @param signal                    signal interface
     * @param markerUpdateInterface     marker updater object
     */
    Marker(int signalId, GraphView.GraphSignalInputInterface graphSignalInputInterface,
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

        float centreX, centreY;

        if(mMarkerPosition <
                mGraphInterface.getGraphXZoomDisplay().getDisplayOffsetPercentage()) {
            centreX = getDrawableArea().getLeft();
        } else if(mMarkerPosition >
                mGraphInterface.getGraphXZoomDisplay().getFarSideOffsetPercentage()) {
            centreX = getDrawableArea().getRight();
        } else {
            centreX = getMarkerPositionInPx();
        }

        float yPosition = (1f - mSignalInterface.getValueAtPosition(xValue));

        if(yPosition < 0) {
            centreY = getDrawableArea().getTop();
        } else if(yPosition > 1) {
            centreY = getDrawableArea().getBottom();
        } else {
            centreY = getDrawableArea().getTop() + yPosition * getDrawableArea().getHeight();
        }

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

    /**
     * @return current position of the marker 0-1
     */
    public float getMarkerPosition() {
        return mMarkerPosition;
    }

    /**
     * update the marker position
     *
     * @param markerPosition a value between 0-1
     */
    public void setMarkerPosition(float markerPosition) {
        if(markerPosition > 1.0f) {
            this.mMarkerPosition = 1.0f;
        } else if(markerPosition < 0.0f) {
            this.mMarkerPosition = 0.0f;
        } else {
            this.mMarkerPosition = markerPosition;
        }
    }

    /**
     * @return the marker position in relation to pixels
     */
    public float getMarkerPositionInPx() {
        float intersect = mMarkerPosition;

        intersect -= mGraphInterface.getGraphXZoomDisplay().getDisplayOffsetPercentage();
        intersect /= mGraphInterface.getGraphXZoomDisplay().getZoomLevelPercentage();

        intersect *= getDrawableArea().getWidth();
        intersect += getDrawableArea().getLeft();

        if(intersect < getDrawableArea().getLeft()) {
            return getDrawableArea().getLeft();
        } else if(intersect > getDrawableArea().getRight()) {
            return getDrawableArea().getRight();
        }

        return intersect;
    }

    /**
     * Set the marker position based on pixels
     *
     * @param markerPositionInPx a value between 0 and width of drawable area
     */
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

    /**
     * If the attached signal is changed please call this with the new interface
     *
     * @param mSignalInterface interface to the new signal
     */
    void setSignalInterface(SignalBufferInterface mSignalInterface) {
        this.mSignalInterface = mSignalInterface;
    }

    /**
     * @return interface to the graph view
     */
    public GraphView.GraphSignalInputInterface getGraphInterface() {
        return mGraphInterface;
    }

    /**
     * @return signal id which the markers are attached to
     */
    int getSignalId() {
        return mSignalId;
    }

    /**
     * The update interface to allow communication to other objects
     */
    public interface MarkerUpdateInterface {
        void markerColour(int color);

        void markerPositionUpdate(float xValue, float yValue);
    }

}
