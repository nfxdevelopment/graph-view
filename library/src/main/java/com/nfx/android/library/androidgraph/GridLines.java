package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 *
 * Extend from this class to create a drawable line with view zooming capabilities
 */
public abstract class GridLines extends DrawableObject {

    /**
     * Indicates that the grid line is less than the viewable area
     */
    static final float LESS_THAN_VIEWABLE_AREA = -1;
    /**
     * Indicates that the grid line is greater than the viewable area
     */
    static final float GREATER_THAN_VIEWABLE_AREA = -2;
    /**
     * When a using asks for a grid line which is not present in this object
     */
    static final float GRID_LINE_OUT_OF_RANGE = -3;
    /**
     * Color of the grid lines
     */
    private static final int INITIAL_LINE_COLOR = Color.GRAY;
    /**
     * Use an even number to ensure all grid line strokes look the same
     */
    private static final float INITIAL_LINE_STROKE_WIDTH = 4f;
    /**
     * Number of grid lines to display in the area
     */
    final int mNumberOfGridLines = 6;
    /**
     * Paint for the grid lines
     */
    final Paint mPaint = new Paint();
    /**
     * This allows us to know the axis at runtime
     */
    private final AxisOrientation mAxisOrientation;
    /**
     * Minor GridLines
     */
    private final Map<Integer, GridLines> mChildGridLines = new ConcurrentHashMap<>();
    /**
     * Describes the viewable part of the grid
     */
    ZoomDisplay mZoomDisplay;
    /**
     * The axis text to be displayed if needed
     */
    AxisText mAxisText;
    /**
     * Graph dimension size, This is needed for minor grid lines to calculate where to display in
     * cases of zoom
     */
    private float mGridLinesSize;
    private float mGridLinesOffset;
    /**
     * Base Context
     */
    private Context mContext;

    /**
     * Constructor of GridLines
     *
     * @param axisOrientation either the x or y axis
     */
    GridLines(AxisOrientation axisOrientation) {
        mAxisOrientation = axisOrientation;
        // Set a default zoom Display
        mZoomDisplay = new ZoomDisplay(1f, 0f);
        mPaint.setColor(INITIAL_LINE_COLOR);
        mPaint.setStrokeWidth(INITIAL_LINE_STROKE_WIDTH);
    }

    @Override
    public void doDraw(Canvas canvas) {
        if (mAxisText != null) {
            mAxisText.doDraw(canvas);
        }
        Iterator<GridLines> iterator = mChildGridLines.values().iterator();
        while(iterator.hasNext()) {
            iterator.next().doDraw(canvas);
        }
    }

    public void showAxisText(Context context, float minimumValue, float maximumValue) {
        mContext = context;
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
    private void setGridStrokeWidth(int strokeWidth) {
        mPaint.setStrokeWidth(strokeWidth);

    }

    /**
     * Change the color of the lines at runtime
     *
     * @param color new color value
     */
    private void setColor(int color) {
        mPaint.setColor(color);
    }

    private float getGridLineSpacingInPixels() {
        // -1 as we want the first grid line to be at 0 and the last at the width of the graph
        return mGridLinesSize / (float) (mNumberOfGridLines - 1);
    }

    /**
     * Gives the intersect point of a grid line a when the zoom level is 100 percent
     *
     * @param gridLine grid line to find out the intersecting value
     * @return intersecting point
     */
    float intersect(int gridLine) {
        if (gridLine >= mNumberOfGridLines || gridLine < 0) {
            return GRID_LINE_OUT_OF_RANGE;
        }

        return mGridLinesOffset + getGridLineSpacingInPixels() * (float) (gridLine);
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
    void setGridLinesSize(float gridLinesSize) {
        mGridLinesSize = gridLinesSize;
    }

    void setGridLinesOffset(float graphOffset) {
        mGridLinesOffset = graphOffset;
    }

    /**
     * As axis text has to attain the drawable area before the grid lines to ensure grid lines do
     * not over lap the text. We have to provide another function. this function has to be called
     * for each grid lines before {@code surfaceChanged} of grid lines
     *
     * @param drawableArea the drawable area available
     */
    public void notifyAxisTextOfSurfaceChange(DrawableArea drawableArea) {
        if (mAxisText != null) {
            mAxisText.surfaceChanged(drawableArea);
        }
        for (GridLines gridLines : mChildGridLines.values()) {
            gridLines.notifyAxisTextOfSurfaceChange(drawableArea);
        }
    }

    /**
     * The surface size has changed update the current object to resize drawing
     *
     * @param drawableArea new surface size
     */
    @Override
    public void surfaceChanged(DrawableArea drawableArea) {
        super.surfaceChanged(drawableArea);
        for (Map.Entry<Integer, GridLines> gridLines : mChildGridLines.entrySet()) {
            gridLines.getValue().surfaceChanged(drawableArea);
            minorGridLineSurfaceChanged(gridLines.getValue(), gridLines.getKey());
        }
    }

    private ZoomDisplay getZoomDisplay() {
        return mZoomDisplay;
    }

    /**
     * Set the zoomDisplay for the grid lines should morph to
     *
     * @param zoomDisplay zoomDisplay to set
     */
    public void setZoomDisplay(ZoomDisplay zoomDisplay) {
        mZoomDisplay = zoomDisplay;

        zoomDisplay.setTheListener(new ZoomDisplay.ZoomChangedListener() {
            @Override
            public void zoomChanged() {
                Map<Integer, Boolean> minorXGridLinesToDisplay =
                        adequateSpaceForMinorGridLines();

                for (Map.Entry<Integer, Boolean> majorGridLine : minorXGridLinesToDisplay
                        .entrySet()) {
                    if (majorGridLine.getValue()) {
                        addMinorGridLine(majorGridLine.getKey());
                    } else {
                        mChildGridLines.remove(majorGridLine.getKey());
                    }
                }

                if(mAxisText != null) {
                    mAxisText.calculateGridLineValues();
                }
            }
        });
    }

    /**
     * Reports if there is adequate space to fit minor grid lines between current grid lines
     *
     * @return a key pair that gives a boolean to show if there is enough space between the grid
     * lines
     */
    private Map<Integer, Boolean> adequateSpaceForMinorGridLines() {
        Map<Integer, Boolean> adequateSpaceList = new HashMap<>();
        float zoomSpacing = getGridLineSpacingInPixels() / mZoomDisplay.getZoomLevelPercentage();

        for (int i = 0; i < getNumberOfGridLines() - 1; ++i) {
            // If the grid lines spacing is greater than this number minor grid lines are added
            float mPlaceMinorGridLinesSize = 500f;
            if (zoomSpacing > mPlaceMinorGridLinesSize) {
                float lowerIntersect = intersectZoomCompensated(i);
                float upperIntersect = intersectZoomCompensated(i + 1);
                if (lowerIntersect != upperIntersect) {
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

    /**
     * Add a child minor Grid Line to this grid line
     *
     * @param majorGridLine the grid line number to insert the minor grid line after
     */
    private void addMinorGridLine(int majorGridLine) {
        if (!mChildGridLines.containsKey(majorGridLine)) {
            GridLines minorGridLine;
            if (mAxisOrientation == AxisOrientation.xAxis) {
                minorGridLine = new LinXGridLines();
            } else {
                minorGridLine = new LinYGridLines();
            }
            minorGridLine.setGridStrokeWidth(2);
            minorGridLine.setColor(Color.DKGRAY);
            mChildGridLines.put(majorGridLine, minorGridLine);

            if (mAxisText != null) {
                minorGridLine.showAxisText(mContext, mAxisText.getMinimumAxisValue(),
                        mAxisText.getMaximumAxisValue());
            }

            minorGridLineSurfaceChanged(minorGridLine, majorGridLine);
            minorGridLine.setZoomDisplay(getZoomDisplay());
            // If we have axis text we want our children to have axis text

        }
    }

    /**
     * this tells the children grid lines where the major grid lines would sit at 100% zoom level in
     * the new surface dimensions
     *
     * @param gridLine      The child grid line
     * @param majorGridLine major grid line the child is sitting on
     */
    private void minorGridLineSurfaceChanged(GridLines gridLine, int majorGridLine) {
        DrawableArea parentDrawableArea = getDrawableArea();
        gridLine.surfaceChanged(parentDrawableArea);

        int left = (int) intersect(majorGridLine);
        int right = (int) intersect(majorGridLine + 1);

        gridLine.surfaceChanged(parentDrawableArea);

        gridLine.setGridLinesSize(right - left);
        gridLine.setGridLinesOffset(left);

        if (mAxisText != null) {
            // We want out children Axis to have the same drawable area as our own.
            gridLine.getAxisText().getDrawableArea().setDrawableArea(mAxisText.getDrawableArea());
        }
    }

    public AxisText getAxisText() {
        return mAxisText;
    }

    enum AxisOrientation {
        xAxis,
        yAxis
    }
}
