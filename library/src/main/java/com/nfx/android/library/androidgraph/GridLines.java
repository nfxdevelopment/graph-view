package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 *
 * Extend from this class to create a drawable lines representing graph lines. This will draw a
 * single axis for the given number of lines.
 */
public abstract class GridLines extends DrawableObject {

    /**
     * When a using asks for a grid line which is not present in this object
     */
    static final float GRID_LINE_OUT_OF_RANGE = -3;
    /**
     * Indicates that the grid line is less than the viewable area
     */
    private static final float LESS_THAN_VIEWABLE_AREA = -1;
    /**
     * Indicates that the grid line is greater than the viewable area
     */
    private static final float GREATER_THAN_VIEWABLE_AREA = -2;
    /**
     * Color of the grid lines
     */
    private static final int INITIAL_LINE_COLOR = Color.GRAY;
    /**
     * Use an even number to ensure all grid line strokes look the same
     */
    private static final float INITIAL_LINE_STROKE_WIDTH = 4f;
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
     * This is a zoom that is never changed over the runtime of the app. Useful for setting limits
     */
    ZoomDisplay mFixedZoomDisplay;
    /**
     * Number of grid lines to display in the area
     */
    int mNumberOfGridLines = 6;
    /**
     * The axis text to be displayed if needed
     */
    AxisText mAxisText;
    float mGridLinesOffset = 0;
    /**
     * scale for child grid lines
     */
    GraphManager.Scale mChildGridLineScale;
    /**
     * Describes the viewable part of the grid
     */
    private ZoomDisplay mZoomDisplay;
    /**
     * Graph dimension size, This is needed for minor grid lines to calculate where to display in
     * cases of zoom
     */
    private float mGridLinesSize;
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
        mFixedZoomDisplay = new ZoomDisplay(1f, 0f);
        // Set a default zoom Display
        mZoomDisplay = new ZoomDisplay(1f, 0f);
        mPaint.setColor(INITIAL_LINE_COLOR);
        mPaint.setStrokeWidth(INITIAL_LINE_STROKE_WIDTH);

        setGridLinesSize(1f);
    }

    /**
     * Super should always be called to ensure the Axis text and any child lines are drawn if needed
     *
     * @param canvas a canvas to draw onto
     */
    @Override
    public void doDraw(Canvas canvas) {
        if (mAxisText != null) {
            mAxisText.doDraw(canvas);
        }
        for(GridLines gridLines : mChildGridLines.values()) {
            gridLines.doDraw(canvas);
        }
    }

    public void showAxisText(Context context, float minimumValue, float maximumValue) {
        mContext = context;
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
     * Set the number of Grid lines for this object
     *
     * @param numberOfGridLines amount of grid lines
     */
    public void setNumberOfGridLines(int numberOfGridLines) {
        mNumberOfGridLines = numberOfGridLines;
    }

    /**
     * Change the stroke width of the lines at runtime
     *
     * @param strokeWidth new stroke width value
     */
    @SuppressWarnings("SameParameterValue")
    private void setGridStrokeWidth(int strokeWidth) {
        mPaint.setStrokeWidth(strokeWidth);

    }

    /**
     * Change the color of the lines at runtime
     *
     * @param color new color value
     */
    @SuppressWarnings("SameParameterValue")
    private void setColor(int color) {
        mPaint.setColor(color);
    }

    float getGridLineDrawableWidth() {
        // -1 as we want the first grid line to be at 0 and the last at the width of the graph
        return mGridLinesSize / (float) (mNumberOfGridLines - 1);
    }

    abstract float intersect(int gridLine);

    /**
     * Gives the value of where a grid line will interest x on the screen
     *
     * @param gridLine        grid line to find, base 0
     * @return the x Intersect
     */
    public float intersectZoomCompensated(int gridLine) {
        float intersect = intersect(gridLine);
        if(intersect == GRID_LINE_OUT_OF_RANGE) {
            return GRID_LINE_OUT_OF_RANGE;
        }

        intersect -= mFixedZoomDisplay.getDisplayOffsetPercentage();
        intersect /= mFixedZoomDisplay.getZoomLevelPercentage();

        intersect -= mZoomDisplay.getDisplayOffsetPercentage();
        intersect /= mZoomDisplay.getZoomLevelPercentage();
        return intersect;
    }

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
    private void setGridLinesSize(float gridLinesSize) {
        mGridLinesSize = gridLinesSize;
    }

    void setGridLinesOffset(float graphOffset) {
        mGridLinesOffset = graphOffset;
    }

    @SuppressWarnings("SameParameterValue")
    public void setChildGridLineScale(GraphManager.Scale scale) {
        mChildGridLineScale = scale;
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

        if(mAxisText != null) {
            mAxisText.calculateGridLineValues();
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
        final float mPlaceMinorGridLinesSize = 500f;

        for (int i = 0; i < getNumberOfGridLines() - 1; ++i) {

            float lowerIntersect = intersectZoomCompensated(i);
            float upperIntersect = intersectZoomCompensated(i + 1);

            if((lowerIntersect > 0 && lowerIntersect < getDimensionLength()) ||
                    (upperIntersect > 0 &&
                            upperIntersect < getDimensionLength())) {
                float gridLineSpacing = (upperIntersect - lowerIntersect) *
                        getDimensionLength();
                // If the grid lines spacing is greater than this number minor grid lines are added
                if(gridLineSpacing > mPlaceMinorGridLinesSize) {
                    adequateSpaceList.put(i, true);
                    continue;
                }
            }

            adequateSpaceList.put(i, false);

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
                if(mChildGridLineScale == GraphManager.Scale.linear) {
                    minorGridLine = new LinXGridLines();
                } else {
                    minorGridLine = new LogXGridLines(mAxisText.getAxisValueSpan());
                }
            } else {
                if(mChildGridLineScale == GraphManager.Scale.linear) {
                    minorGridLine = new LinYGridLines();
                } else {
                    minorGridLine = new LogYGridLines(mAxisText.getAxisValueSpan());
                }
            }
            minorGridLine.setGridStrokeWidth(2);
            minorGridLine.setColor(Color.DKGRAY);
            minorGridLine.setNumberOfGridLines(10);
            minorGridLine.setFixedZoomDisplay(mFixedZoomDisplay);
            mChildGridLines.put(majorGridLine, minorGridLine);


            if (mAxisText != null) {
                minorGridLine.showAxisText(mContext, mAxisText.getMinimumAxisValue(),
                        mAxisText.getMaximumAxisValue());
            }

            minorGridLineSurfaceChanged(minorGridLine, majorGridLine);
            minorGridLine.setZoomDisplay(getZoomDisplay());
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

        float left = intersect(majorGridLine);
        float right = intersect(majorGridLine + 1);

        gridLine.surfaceChanged(parentDrawableArea);

        gridLine.setGridLinesSize(right - left);
        gridLine.setGridLinesOffset(left);

        if (mAxisText != null) {
            // We want out children Axis to have the same drawable area as our own.
            gridLine.getAxisText().getDrawableArea().setDrawableArea(mAxisText.getDrawableArea());
            gridLine.getAxisText().calculateGridLineValues();
        }
    }

    /**
     * Used to report back the height for the yAxis or width on the xAxis
     *
     * @return dimension length
     */
    abstract float getDimensionLength();

    private AxisText getAxisText() {
        return mAxisText;
    }

    public ZoomDisplay getFixedZoomDisplay() {
        return mFixedZoomDisplay;
    }

    public void setFixedZoomDisplay(ZoomDisplay mFixedZoomDisplay) {
        this.mFixedZoomDisplay = mFixedZoomDisplay;
    }

    enum AxisOrientation {
        xAxis,
        yAxis
    }
}
