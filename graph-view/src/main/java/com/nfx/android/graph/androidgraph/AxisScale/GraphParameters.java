package com.nfx.android.graph.androidgraph.AxisScale;

import com.nfx.android.graph.androidgraph.Scale;

/**
 * NFX Development
 * Created by nick on 12/08/16.
 * <p>
 * An object that holds the information for both axis of the graph
 */
public class GraphParameters {
    private AxisParameters xAxisParameters = new AxisParameters(0, 1, Scale.linear);
    private AxisParameters yAxisParameters = new AxisParameters(0, 1, Scale.linear);

    public GraphParameters(){}

    public GraphParameters(AxisParameters xAxisParameters, AxisParameters yAxisParameters) {
        this.xAxisParameters = xAxisParameters;
        this.yAxisParameters = yAxisParameters;
    }

    /**
     * @return x axis parameters
     */
    public AxisParameters getXAxisParameters() {
        return xAxisParameters;
    }

    /**
     * @return y axis parameters
     */
    public AxisParameters getYAxisParameters() {
        return yAxisParameters;
    }
}
