package com.nfx.android.library.androidgraph.AxisScale;

import com.nfx.android.library.androidgraph.Scale;

/**
 * NFX Development
 * Created by nick on 12/08/16.
 */
public class GraphParameters {
    private AxisParameters mXAxisParameters = new AxisParameters(0, 1, Scale.linear);
    private AxisParameters mYAxisParameters = new AxisParameters(0, 1, Scale.linear);

    public AxisParameters getXAxisParameters() {
        return mXAxisParameters;
    }

    public AxisParameters getYAxisParameters() {
        return mYAxisParameters;
    }
}
