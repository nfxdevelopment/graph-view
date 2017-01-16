package com.nfx.android.graph.androidgraph;

import com.nfx.android.graph.androidgraph.AxisScale.GraphParameters;

/**
 * NFX Development
 * Created by nick on 15/01/17.
 */
public interface GraphViewInterface {

    void start();

    void stop();

    ZoomDisplay getGraphXZoomDisplay();

    ZoomDisplay getGraphYZoomDisplay();

    GraphParameters getGraphParameters();

    DrawableArea getDrawableArea();
}
