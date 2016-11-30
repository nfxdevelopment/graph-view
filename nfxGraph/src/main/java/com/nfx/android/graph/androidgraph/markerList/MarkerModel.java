package com.nfx.android.graph.androidgraph.markerList;

import android.graphics.Color;

import com.nfx.android.graph.androidgraph.GraphView;
import com.nfx.android.graph.androidgraph.Marker;

/**
 * NFX Development
 * Created by nick on 5/08/16.
 * <p/>
 * An object that holds the values to display in the floating widgets.
 */
public class MarkerModel implements Marker.MarkerUpdateInterface {
    private final MarkerAdapter parentAdapter;
    private final GraphView.GraphSignalInputInterface graphSignalInputInterface;
    private float xValue = 0;
    private float yValue = 0;
    private int markerColor = Color.BLACK;

    public MarkerModel(MarkerAdapter parentAdapter,
                       GraphView.GraphSignalInputInterface graphSignalInputInterface) {
        this.parentAdapter = parentAdapter;
        this.graphSignalInputInterface = graphSignalInputInterface;
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

    @Override
    public void markerPositionUpdate(float xValue, float yValue) {
        // The buffer has been converted to decibels, flipped, then scaled to the minimum value.
        // So we need to reverse this, retrieve and convert back to the decibel value
        this.xValue = xValue;
        yValue *= graphSignalInputInterface.getGraphYZoomDisplay().getZoomLevelPercentage();
        yValue += graphSignalInputInterface.getGraphYZoomDisplay().getDisplayOffsetPercentage();
        this.yValue = (1f - yValue) * graphSignalInputInterface.getGraphParameters().
                getYAxisParameters().getMinimumValue();
        parentAdapter.refreshList();
    }
}
