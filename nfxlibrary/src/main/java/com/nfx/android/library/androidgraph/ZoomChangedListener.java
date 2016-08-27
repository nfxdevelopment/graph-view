package com.nfx.android.library.androidgraph;

import java.util.Observable;
import java.util.Observer;

/**
 * NFX Development
 * Created by nick on 15/07/16.
 * <p/>
 * An interface for subscribes to get information about view zoom level changes
 */
abstract class ZoomChangedListener implements Observer {
    abstract void zoomChanged();

    public void update(Observable obs, Object obj) {
        zoomChanged();
    }
}