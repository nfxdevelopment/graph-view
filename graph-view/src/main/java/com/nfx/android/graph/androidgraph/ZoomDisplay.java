package com.nfx.android.graph.androidgraph;

import android.support.annotation.NonNull;

import java.util.Observable;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 * <p>
 * A helper class which will hold the zoom values of a given view. A listener can get updates
 * when the surface size has changed.
 */
public class ZoomDisplay extends Observable {
    /**
     * Absolute minimum zoom level
     */
    private static final float MINIMUM_ZOOM_LEVEL = 0f;
    /**
     * Absolute maximum zoom value
     */
    private static final float MAXIMUM_ZOOM_LEVEL = 1f;
    /**
     * Maximum zoom level
     */
    private float maximumZoomLevel = 1f;
    /**
     * Minimum zoom level
     */
    private float minimumZoomLevel = 0f;
    /**
     * percentage of view to display in given area
     */
    private float zoomLevelPercentage = 1f;
    /**
     * offset in given plane where to display from
     */
    private float displayOffsetPercentage = 0f;


    /**
     * @param zoomLevelPercentage     a float referenced as 0% = 0 100% = 1 if outside this value a
     *                                warning is logged and 1 will be assigned
     * @param displayOffsetPercentage a float referenced as 0% = 0 100% = 1 if outside this value a
     *                                warning is logged and 0f will be assigned
     **/
    public ZoomDisplay(float zoomLevelPercentage, float displayOffsetPercentage) {
        if(zoomLevelPercentage < maximumZoomLevel && zoomLevelPercentage > minimumZoomLevel) {
            this.zoomLevelPercentage = zoomLevelPercentage;
        }
        if(displayOffsetPercentage < maximumZoomLevel && displayOffsetPercentage >
                minimumZoomLevel) {
            // Ensure that the zoom level will be within the bounds of the screen
            if((displayOffsetPercentage + this.zoomLevelPercentage) > maximumZoomLevel) {
                this.displayOffsetPercentage = maximumZoomLevel - this.zoomLevelPercentage;
            } else {
                this.displayOffsetPercentage = displayOffsetPercentage;
            }
        }
    }

    /**
     * register a listener for zoom changed reports
     * @param listener register listener
     */
    void addListener(ZoomChangedListener listener) {
        addObserver(listener);
    }

    /**
     * remove a listener for zoom changed reports
     *
     * @param listener remove from listener
     */
    void removeListener(ZoomChangedListener listener) {
        deleteObserver(listener);
    }

    /**
     * @return the value of displayOffsetPercentage
     */
    public float getDisplayOffsetPercentage() {
        return displayOffsetPercentage;
    }

    /**
     * change the view offset
     *
     * @param displayOffsetPercentage a float referenced as a percentage across the screen 0% = 0
     *                                100% = 1 if outside this value a warning is logged and
     *                                nothing happens
     */
    public void setDisplayOffsetPercentage(float displayOffsetPercentage) {
        this.displayOffsetPercentage = displayOffsetPercentage;

        setChanged();
        notifyObservers();
    }

    /**
     * @return the value of displayOffsetPercentage + size currently being displayed
     */
    public float getFarSideOffsetPercentage() {
        return displayOffsetPercentage + zoomLevelPercentage;
    }

    /**
     * @return the value of zoomLevelPercentage
     */
    public float getZoomLevelPercentage() {
        return zoomLevelPercentage;
    }

    /**
     * change the zoom level of the view
     * @param zoomLevelPercentage a float referenced as 0% = 0 100% = 1 if outside this value a
     *                            warning is logged and nothing happens
     */
    public void setZoomLevelPercentage(float zoomLevelPercentage) {
        if(zoomLevelPercentage < MINIMUM_ZOOM_LEVEL) {
            return;
        } else if(zoomLevelPercentage > MAXIMUM_ZOOM_LEVEL) {
            zoomLevelPercentage = MAXIMUM_ZOOM_LEVEL;
        }

        this.zoomLevelPercentage = zoomLevelPercentage;

        setChanged();
        notifyObservers();
    }

    /**
     * Used to ensure zoom does not stray outside a set limit
     *
     * @param minimumZoomLevel minimum zoom possible
     * @param maximumZoomLevel maximum zoom possible
     */
    public void setZoomLimits(float minimumZoomLevel, float maximumZoomLevel) {
        this.minimumZoomLevel = minimumZoomLevel;
        this.maximumZoomLevel = maximumZoomLevel;
        setZoomLevelPercentage(maximumZoomLevel - minimumZoomLevel);
        setDisplayOffsetPercentage(minimumZoomLevel);
    }

    /**
     * copy across all value from another zoom display object
     *
     * @param zoomDisplay Object to copy values from
     */
    public void setZoomDisplay(@NonNull ZoomDisplay zoomDisplay) {
        this.displayOffsetPercentage = zoomDisplay.displayOffsetPercentage;
        this.zoomLevelPercentage = zoomDisplay.zoomLevelPercentage;
        this.minimumZoomLevel = zoomDisplay.minimumZoomLevel;
        this.maximumZoomLevel = zoomDisplay.maximumZoomLevel;
    }

    /**
     * @return maximum zoom level possible by this object
     */
    public float getMaximumZoomLevel() {
        return maximumZoomLevel;
    }

    /**
     * @return minimum zoom level possible by this object
     */
    public float getMinimumZoomLevel() {
        return minimumZoomLevel;
    }
}
