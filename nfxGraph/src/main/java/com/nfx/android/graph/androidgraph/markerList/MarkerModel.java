package com.nfx.android.graph.androidgraph.markerList;

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
public class MarkerModel implements Marker.MarkerUpdateInterface {
    private final MarkerAdapter parentAdapter;
    private final GraphView.GraphSignalInputInterface graphSignalInputInterface;
    private final SignalBuffer signalBuffer;
    private float xValue = 0;
    private float yValue = 0;
    private int markerColor = Color.BLACK;

    public MarkerModel(MarkerAdapter parentAdapter,
                       GraphView.GraphSignalInputInterface graphSignalInputInterface,
                       SignalBuffer signalBuffer) {
        this.parentAdapter = parentAdapter;
        this.graphSignalInputInterface = graphSignalInputInterface;
        this.signalBuffer = signalBuffer;
    }

    float getXValue() {
        return xValue;
    }

    float getYValue() {
        return yValue;
    }

    int getMarkerColor() {
        return markerColor;
    }

    @Override
    public void markerColour(int color) {
        this.markerColor = color;
        parentAdapter.refreshList();
    }

    // TODO may have broken specscope because of this change
    @Override
    public void markerPositionUpdate(float xValue, float yValue) {
        // The buffer has been converted to decibels, flipped, then scaled to the minimum value.
        // So we need to reverse this, retrieve and convert back to the decibel value
        this.xValue = xValue;
        yValue *= signalBuffer.getYZoomDisplay().getZoomLevelPercentage();
        yValue += signalBuffer.getYZoomDisplay().getDisplayOffsetPercentage();
        float miniMumValue = graphSignalInputInterface.getGraphParameters().
                getYAxisParameters().getMinimumValue();
        float axisSpan = graphSignalInputInterface.getGraphParameters().
                getYAxisParameters().getAxisSpan();
        this.yValue = miniMumValue + (1f - yValue) * axisSpan;
        parentAdapter.refreshList();
    }
}
