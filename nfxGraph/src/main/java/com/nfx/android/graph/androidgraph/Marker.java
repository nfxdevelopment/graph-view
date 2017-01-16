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
    private final GraphViewInterface graphViewInterface;
    /**
     * Post updates to this object
     */
    private final MarkerUpdateInterface markerUpdateInterface;
    /**
     * Id of the signal
     */
    private final int signalId;
    /**
     * interface to signal
     */
    private SignalBufferInterface signalInterface;
    /**
     * Size of the circle
     */
    private float circleRadius = 20f;
    /**
     * Default position for the marker
     */
    private float markerPosition = (float) Math.random();

    /**
     * @param signalId                  Id of the signal the markers are attached to
     * @param graphViewInterface interface to the graph view
     * @param signal                    signal interface
     * @param markerUpdateInterface     marker updater object
     */
    Marker(int signalId, GraphViewInterface graphViewInterface,
           SignalBufferInterface signal, MarkerUpdateInterface markerUpdateInterface) {
        this.signalId = signalId;
        this.graphViewInterface = graphViewInterface;
        this.signalInterface = signal;
        this.markerUpdateInterface = markerUpdateInterface;
        paint.setColor(INITIAL_LINE_COLOR);
        paint.setStrokeWidth(INITIAL_LINE_STROKE_WIDTH);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        this.markerUpdateInterface.markerColour(INITIAL_LINE_COLOR);
    }

    @Override
    public void doDraw(Canvas canvas) {
        float xValue = graphViewInterface.getGraphParameters().getXAxisParameters().
                graphPositionToScaledAxis(markerPosition);
        float yValue = signalInterface.getValueAtPosition(xValue);
        markerUpdateInterface.markerPositionUpdate(xValue, yValue);

        float centreX, centreY;

        if(markerPosition <
                graphViewInterface.getGraphXZoomDisplay().getDisplayOffsetPercentage()) {
            centreX = getDrawableArea().getLeft();
        } else if(markerPosition >
                graphViewInterface.getGraphXZoomDisplay().getFarSideOffsetPercentage()) {
            centreX = getDrawableArea().getRight();
        } else {
            centreX = getMarkerPositionInPx();
        }

        float yPosition = (1f - signalInterface.getValueAtPosition(xValue));

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
                circleRadius,
                paint
        );
        canvas.drawLine(centreX - circleRadius,
                centreY,
                centreX + circleRadius,
                centreY,
                paint
        );
        canvas.drawLine(centreX,
                centreY - circleRadius,
                centreX,
                centreY + circleRadius,
                paint
        );
    }

    @Override
    public void surfaceChanged(DrawableArea drawableArea) {
        float dimensionDivisor = 30f;
        if(drawableArea.getHeight() < drawableArea.getWidth()) {
            circleRadius = drawableArea.getHeight() / dimensionDivisor;
        } else {
            circleRadius = drawableArea.getWidth() / 30;
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
        return markerPosition;
    }

    /**
     * update the marker position
     *
     * @param markerPosition a value between 0-1
     */
    public void setMarkerPosition(float markerPosition) {
        if(markerPosition > 1.0f) {
            this.markerPosition = 1.0f;
        } else if(markerPosition < 0.0f) {
            this.markerPosition = 0.0f;
        } else {
            this.markerPosition = markerPosition;
        }
    }

    /**
     * @return the marker position in relation to pixels
     */
    float getMarkerPositionInPx() {
        float intersect = markerPosition;

        intersect -= graphViewInterface.getGraphXZoomDisplay().getDisplayOffsetPercentage();
        intersect /= graphViewInterface.getGraphXZoomDisplay().getZoomLevelPercentage();

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

        markerPosition += graphViewInterface.getGraphXZoomDisplay().getDisplayOffsetPercentage();
        markerPosition *= graphViewInterface.getGraphXZoomDisplay().getZoomLevelPercentage();

        setMarkerPosition(markerPosition);
    }

    @Override
    public void setColour(int colour) {
        super.setColour(colour);
        markerUpdateInterface.markerColour(colour);
    }

    /**
     * If the attached signal is changed please call this with the new interface
     *
     * @param mSignalInterface interface to the new signal
     */
    void setSignalInterface(SignalBufferInterface mSignalInterface) {
        this.signalInterface = mSignalInterface;
    }

    /**
     * @return interface to the graph view
     */
    public GraphViewInterface getGraphViewInterface() {
        return graphViewInterface;
    }

    /**
     * @return signal id which the markers are attached to
     */
    int getSignalId() {
        return signalId;
    }

    /**
     * The update interface to allow communication to other objects
     */
    public interface MarkerUpdateInterface {
        void markerColour(int color);

        void markerPositionUpdate(float xValue, float yValue);
    }

}
