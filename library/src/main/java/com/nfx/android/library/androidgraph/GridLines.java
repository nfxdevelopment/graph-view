package com.nfx.android.library.androidgraph;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 *
 * Extend from this class to create a drawable line with view zooming capabilities
 */
public abstract class GridLines extends DrawableObject {

    /**
     * Number of grid lines to display in the area
     */
    protected int mNumberOfGridLines = 8;
    /**
     * Color of the grid lines
     */
    protected int mGridColor = Color.GRAY;
    /**
     * Use an even number to ensure all grid line strokes look the same
     */
    protected float mGridStrokeWidth = 4f;
    /**
     * Describes the viewable part of the grid
     */
    protected ZoomDisplay mZoomDisplay;
    /**
     * Graph dimension size, This is needed for minor grid lines to calculate where to display in
     * cases of zoom
     */
    protected float mGridLinesSize;
    protected float mGridLinesOffset;
    /**
     * This allows us to know the axis at runtime
     */
    private AxisOrientation mAxisOrientation;
    /**
     * If the grid lines spacing is greater than this number minor gridlines are added
     */
    private float mPlaceMinorGridLinesSize = 500f;

    /**
     * Constructor of GridLines
     *
     * @param axisOrientation either the x or y axis
     */
    public GridLines(AxisOrientation axisOrientation) {
        mAxisOrientation = axisOrientation;
        // Set a default zoom Display
        mZoomDisplay = new ZoomDisplay(1f, 0f);
    }

    /**
     * Gets the axis object is references
     *
     * @return a enum value for current axis
     */
    public AxisOrientation getAxisOrientation() {
        return mAxisOrientation;
    }

    /**
     * Number of grid lines at 100% zoom
     *
     * @return current number of grid lines
     */
    public int getNumberOfGridLines() {
        return mNumberOfGridLines;
    }

    /**
     * Change the stroke width of the lines at runtime
     *
     * @param strokeWidth new stroke width value
     */
    public void setGridStrokeWidth(int strokeWidth) {
        mGridStrokeWidth = strokeWidth;
    }

    /**
     * Change the color of the lines at runtime
     *
     * @param color new color value
     */
    public void setColor(int color) {
        mGridColor = color;
    }

    /**
     * Gives the intersect point of a grid line a when the zoom level is 100 percent
     *
     * @param gridLine grid line to find out the intersecting value
     * @return intersecting point
     */
    public float intersect(int gridLine) {
        if (gridLine >= mNumberOfGridLines || gridLine < 0) {
            return -1f;
        }

        // +1 as there would be mNumberOfGridLines intersecting the graph which splits
        // mNumberOfGridLines + 1 areas
        float spacing = (mGridLinesSize) / (float) (mNumberOfGridLines + 1);
        // The first line lies at on the area boundary of the first block hence +1
        // This is the value where it would intersectZoomCompensated if zoom was at 100%
        return mGridLinesOffset + spacing * (float) (gridLine + 1);
    }

    /**
     * To be implemented when axis orientation is know
     *
     * @param gridLine grid line to find out the intersecting value
     * @return intersecting point
     */
    public abstract float intersectZoomCompensated(int gridLine);

    /**
     * To be implemented when scale is know LOG/LIN
     *
     * @param gridLine grid line to find out the intersecting value
     * @param dimensionLength length of width or height
     * @return intersecting point
     */
    protected abstract float intersectZoomCompensated(int gridLine, int dimensionLength);

    /**
     * The grid lines are a underlay and is considered a underlay there we do not change the
     * drawable area.
     *
     * @param currentDrawableArea will reflect the new drawable area pass in current drawableArea
     */
    @Override
    public void calculateRemainingDrawableArea(DrawableArea currentDrawableArea) {
    }

    /**
     * Used for minor grid lines to gain a reference to the graph size. Must call after surface
     * changed if used
     *
     * @param gridLinesSize size of the full graph viewable area
     */
    public void setGridLinesSize(float gridLinesSize) {
        mGridLinesSize = gridLinesSize;
    }

    public void setGridLinesOffset(float graphOffset) {
        mGridLinesOffset = graphOffset;
    }

    /**
     * Reports if there is adequate space to fit minor grid lines between current grid lines
     *
     * @return a key pair that gives a boolean to show if there is enough space between the grid
     * lines
     */
    public Map<Integer, Boolean> adequateSpaceForMinorGridLines() {
        Map<Integer, Boolean> adequateSpaceList = new HashMap<>();
        float spacing = (mGridLinesSize) / (float) (mNumberOfGridLines + 1);
        float zoomSpacing = spacing / mZoomDisplay.getZoomLevelPercentage();

        for (int i = 0; i < getNumberOfGridLines() + 1; ++i) {
            if (zoomSpacing > mPlaceMinorGridLinesSize) {
                if ((intersectZoomCompensated(i - 1) > -1 || intersectZoomCompensated(i) > -1) &&
                        intersectZoomCompensated(i - 1) < getDrawableArea().getWidth()) {
                    adequateSpaceList.put(i, true);
                } else {
                    adequateSpaceList.put(i, false);
                }
            } else {
                adequateSpaceList.put(i, false);
            }
        }

        return adequateSpaceList;
    }

    public ZoomDisplay getZoomDisplay() {
        return mZoomDisplay;
    }

    public void setZoomDisplay(ZoomDisplay zoomDisplay) {
        mZoomDisplay = zoomDisplay;
    }

    enum AxisOrientation {
        xAxis,
        yAxis
    }
}
