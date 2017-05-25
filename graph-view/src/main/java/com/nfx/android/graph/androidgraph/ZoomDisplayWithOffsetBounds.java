package com.nfx.android.graph.androidgraph;

/**
 * NFX Development
 * Created by nick on 25/05/17.
 */

public class ZoomDisplayWithOffsetBounds extends ZoomDisplay {
    /**
     * @param zoomLevelPercentage     a float referenced as 0% = 0 100% = 1 if outside this value a
     *                                warning is logged and 1 will be assigned
     * @param displayOffsetPercentage a float referenced as 0% = 0 100% = 1 if outside this value a
     **/
    public ZoomDisplayWithOffsetBounds(float zoomLevelPercentage, float displayOffsetPercentage) {
        super(zoomLevelPercentage, displayOffsetPercentage);
    }

    /**
     * change the view offset
     *
     * @param displayOffsetPercentage a float referenced as a percentage across the screen 0% = 0
     *                                100% = 1 if outside this value a warning is logged and
     *                                nothing happens
     */
    public void setDisplayOffsetPercentage(float displayOffsetPercentage) {
        if(displayOffsetPercentage < getMinimumZoomLevel()) {
            displayOffsetPercentage = getMinimumZoomLevel();
        } else if(displayOffsetPercentage > getMaximumZoomLevel()) {
            displayOffsetPercentage = getMaximumZoomLevel();
        }

        // Ensure that the zoom level will be within the bounds of the screen
        if((displayOffsetPercentage + getZoomLevelPercentage()) > getMaximumZoomLevel()) {
            displayOffsetPercentage = getMaximumZoomLevel() - getZoomLevelPercentage();
        }

        super.setDisplayOffsetPercentage(displayOffsetPercentage);
    }

    /**
     * change the zoom level of the view
     * @param zoomLevelPercentage a float referenced as 0% = 0 100% = 1 if outside this value a
     *                            warning is logged and nothing happens
     */
    public void setZoomLevelPercentage(float zoomLevelPercentage) {
        float displayOffsetPercentage = getDisplayOffsetPercentage();
        if(zoomLevelPercentage < getMinimumZoomLevel()) {
            return;
        } else if(zoomLevelPercentage > getMaximumZoomLevel()) {
            zoomLevelPercentage = getMaximumZoomLevel();
        }

        // Ensure that the zoom level will be within the bounds of the screen
        if((zoomLevelPercentage + getDisplayOffsetPercentage()) > getMaximumZoomLevel()) {
            displayOffsetPercentage = getMaximumZoomLevel() - zoomLevelPercentage;
        }
        if((getMaximumZoomLevel() - zoomLevelPercentage) < getMinimumZoomLevel()) {
            displayOffsetPercentage = getMinimumZoomLevel();
            zoomLevelPercentage = getMaximumZoomLevel() - getMinimumZoomLevel();
        }

        super.setDisplayOffsetPercentage(displayOffsetPercentage);
        super.setZoomLevelPercentage(zoomLevelPercentage);
    }
}
