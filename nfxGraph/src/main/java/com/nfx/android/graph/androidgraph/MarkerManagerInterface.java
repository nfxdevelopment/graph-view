package com.nfx.android.graph.androidgraph;

import android.support.annotation.Nullable;

/**
 * NFX Development
 * Created by nick on 15/01/17.
 */
public interface MarkerManagerInterface {
    void addMarker(int signalId, int colour);

    void updateMarkers(int signalId);

    void removeMarkers(int signalId);

    void representXAsFloat();

    void representYAsFloat();

    void representXAsInteger();

    void representYAsInteger();

    @Nullable
    Marker markerWithinCatchmentArea(float positionX, float catchmentArea);
}
