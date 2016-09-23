package com.nfx.android.library.androidgraph.AxisScale;

import com.nfx.android.library.androidgraph.Scale;

/**
 * NFX Development
 * Created by nick on 12/08/16.
 * <p/>
 * An object that holds the information for both axis of the graph
 */
public class GraphParameters {
    private final AxisParameters mXAxisParameters = new AxisParameters(0, 1, Scale.linear);
    private final AxisParameters mYAxisParameters = new AxisParameters(0, 1, Scale.linear);

    /**
     * @return x axis parameters
     */
    public AxisParameters getXAxisParameters() {
        return mXAxisParameters;
    }

    /**
     * @return y axis parameters
     */
    public AxisParameters getYAxisParameters() {
        return mYAxisParameters;
    }
}
