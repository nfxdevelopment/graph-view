package com.nfx.android.library.androidgraph.markerList;

import android.graphics.Color;

import com.nfx.android.library.androidgraph.GraphView;
import com.nfx.android.library.androidgraph.Marker;

/**
 * NFX Development
 * Created by nick on 5/08/16.
 */
public class MarkerModel implements Marker.MarkerUpdateInterface {
    private MarkerAdapter mParentAdapter;
    private GraphView.GraphSignalInputInterface mGraphSignalInputInterface;
    private float xValue = 0;
    private float yValue = 0;
    private int markerColor = Color.BLACK;

    public MarkerModel(MarkerAdapter parentAdapter,
                       GraphView.GraphSignalInputInterface graphSignalInputInterface) {
        this.mParentAdapter = parentAdapter;
        this.mGraphSignalInputInterface = graphSignalInputInterface;
    }

    public float getXValue() {
        return xValue;
    }

    public void setXValue(float xValue) {
        this.xValue = xValue;
    }

    public float getYValue() {
        return yValue;
    }

    public void setYValue(float yValue) {
        this.yValue = yValue;
    }

    public int getMarkerColor() {
        return markerColor;
    }

    public void setMarkerColor(int markerColor) {
        this.markerColor = markerColor;
    }

    @Override
    public void markerColour(int color) {
        this.markerColor = color;
        mParentAdapter.refreshList();
    }

    @Override
    public void markerPositionUpdate(float xValue, float yValue) {
        // The buffer has been converted to decibels, flipped, then scaled to the minimum value.
        // So we need to reverse this, retrieve and convert back to the decibel value
        this.xValue = xValue;
        yValue *= mGraphSignalInputInterface.getGraphYZoomDisplay().getZoomLevelPercentage();
        yValue += mGraphSignalInputInterface.getGraphYZoomDisplay().getDisplayOffsetPercentage();
        this.yValue = (1f - yValue) * mGraphSignalInputInterface.getGraphParameters().
                getYAxisParameters().getMinimumValue();
        mParentAdapter.refreshList();
    }
}
