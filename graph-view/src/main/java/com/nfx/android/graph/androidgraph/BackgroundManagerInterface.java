package com.nfx.android.graph.androidgraph;

/**
 * NFX Development
 * Created by nick on 16/01/17.
 */
public interface BackgroundManagerInterface {
    GridLines getXGridLines();

    GridLines getYGridLines();

    void setBackgroundColour(int color);

    void setGridLineColour(int color);
}
