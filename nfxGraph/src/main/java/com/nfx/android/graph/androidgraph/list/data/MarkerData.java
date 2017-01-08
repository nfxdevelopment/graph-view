package com.nfx.android.graph.androidgraph.list.data;

import android.graphics.Color;

import com.nfx.android.graph.androidgraph.GraphView;
import com.nfx.android.graph.androidgraph.Marker;
import com.nfx.android.graph.androidgraph.SignalBuffer;

/**
 * NFX Development
 * Created by nick on 5/08/16.
 * <p/>
 * An object that holds the values to display in the floating widgets.
 */
public class MarkerData implements Marker.MarkerUpdateInterface {
    private final GraphView.GraphSignalInputInterface graphSignalInputInterface;
    private final SignalBuffer signalBuffer;
    private float xValue = 0;
    private float yValue = 0;
    private int markerColor = Color.BLACK;

    private boolean xIsInteger = false;
    private boolean yIsInteger = false;

    public MarkerData(GraphView.GraphSignalInputInterface graphSignalInputInterface,
                      SignalBuffer signalBuffer) {
        this.graphSignalInputInterface = graphSignalInputInterface;
        this.signalBuffer = signalBuffer;
    }

    public float getXValue() {
        return xValue;
    }

    public float getYValue() {
        return yValue;
    }

    public int getMarkerColor() {
        return markerColor;
    }

    @Override
    public void markerColour(int color) {
        this.markerColor = color;
    }

    @Override
    public void markerPositionUpdate(float xValue, float yValue) {
        // The buffer has been converted to values relevant to the graph being displayed on
        // so it has been flipped, then scaled to the minimum value.
        // So we need to reverse this, retrieve and convert back to the decibel value
        this.xValue = xValue;
        yValue *= signalBuffer.getYZoomDisplay().getZoomLevelPercentage();
        yValue += signalBuffer.getYZoomDisplay().getDisplayOffsetPercentage();
        float miniMumValue = graphSignalInputInterface.getGraphParameters().
                getYAxisParameters().getMinimumValue();
        float axisSpan = graphSignalInputInterface.getGraphParameters().
                getYAxisParameters().getAxisSpan();
        this.yValue = miniMumValue + yValue * axisSpan;
    }

    public void setXIsInteger(boolean xIsInteger) {
        this.xIsInteger = xIsInteger;
    }

    public void setYIsInteger(boolean yIsInteger) {
        this.yIsInteger = yIsInteger;
    }

    public boolean xIsInteger() {
        return xIsInteger;
    }

    public boolean yIsInteger() {
        return yIsInteger;
    }
}
