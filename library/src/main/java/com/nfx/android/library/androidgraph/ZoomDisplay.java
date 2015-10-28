package com.nfx.android.library.androidgraph;

import android.util.Log;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 */
public class ZoomDisplay {
    private static final String TAG = "ZoomDisplay";

    /**
     * percentage of view to display in given area
     */
    private float mZoomLevelPercentage = 1f;
    /**
     * offset in given plane where to display from
     */
    private float mDisplayOffsetPercentage = 0f;

    /**
     * Subscribe to this listener to get Zoom notification changes
     */
    private ZoomChangedListener mZoomChangedListener;

    /**
     * Initial values of the zoom level
     *
     * @param zoomLevelPercentage     a float referenced as 0% = 0 100% = 1 if outside this value a
     *                                warning is logged and 1 will be assigned
     * @param displayOffsetPercentage a float referenced as 0% = 0 100% = 1 if outside this value a
     *                                warning is logged and 0f will be assigned
     **/
    ZoomDisplay(float zoomLevelPercentage, float displayOffsetPercentage) {
        if (zoomLevelPercentage < 1f && zoomLevelPercentage > 0f) {
            mZoomLevelPercentage = zoomLevelPercentage;
        }
        if (displayOffsetPercentage < 1f && displayOffsetPercentage > 0f) {
            // Ensure that the zoom level will be within the bounds of the screen
            if ((displayOffsetPercentage + mZoomLevelPercentage) > 1f) {
                mDisplayOffsetPercentage = 1f - mZoomLevelPercentage;
            } else {
                mDisplayOffsetPercentage = displayOffsetPercentage;
            }
        }
    }

    /**
     * register a listener for zoom changed reports
     *
     * @param listener register listener
     */
    public void setTheListener(ZoomChangedListener listener) {
        mZoomChangedListener = listener;
    }

    /**
     * change the zoom level of the view
     *
     * @param zoomLevelPercentage a float referenced as 0% = 0 100% = 1 if outside this value a
     *                            warning is logged and nothing happens
     */
    public void setZoomLevelPercentage(float zoomLevelPercentage) {
        if (zoomLevelPercentage < 0f || zoomLevelPercentage > 1f) {
            Log.w(TAG, "zoom level out of bounds 0-1");
            return;
        }

        // Ensure that the zoom level will be within the bounds of the screen
        if ((zoomLevelPercentage + mDisplayOffsetPercentage) > 1f) {
            mDisplayOffsetPercentage = 1f - zoomLevelPercentage;
        }

        mZoomLevelPercentage = zoomLevelPercentage;

        if (mZoomChangedListener != null) {
            mZoomChangedListener.zoomChanged();
        }
    }

    /**
     * change the view offset
     *
     * @param displayOffsetPercentage a float referenced as a percentage across the screen 0% = 0
     *                                100% = 1 if outside this value a warning is logged and
     *                                nothing happens
     */
    public void setDisplayOffsetPercentage(float displayOffsetPercentage) {
        if (displayOffsetPercentage < 0f || displayOffsetPercentage > 1f) {
            Log.w(TAG, "zoom display offset out of bounds 0-1");
            return;
        }

        // Ensure that the zoom level will be within the bounds of the screen
        if ((displayOffsetPercentage + mZoomLevelPercentage) > 1f) {
            mDisplayOffsetPercentage = 1f - mZoomLevelPercentage;
        } else {
            mDisplayOffsetPercentage = displayOffsetPercentage;
        }

        if (mZoomChangedListener != null) {
            mZoomChangedListener.zoomChanged();
        }
    }

    /**
     * An interface for subscribes to get information about view zoom level changes
     */
    public interface ZoomChangedListener {
        void zoomChanged();
    }
}
