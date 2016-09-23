package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.SparseBooleanArray;

import com.nfx.android.library.androidgraph.AxisScale.AxisParameters;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NFX Development
 * Created by nick on 27/10/15.
 *
 * Extend from this class to create a drawable lines representing graph lines. This will draw a
 * single axis for the given number of lines.
 */
abstract class GridLines extends DrawableObject {

    /**
     * When a using asks for a grid line which is not present in this object
     */
    static final float GRID_LINE_OUT_OF_RANGE = -3;
    /**
     * Indicates that the grid line is less than the viewable area
     */
    @SuppressWarnings("unused")
    private static final float LESS_THAN_VIEWABLE_AREA = -1;
    /**
     * Indicates that the grid line is greater than the viewable area
     */
    @SuppressWarnings("unused")
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
     * Graph Scale limits
     */
    final AxisParameters mAxisParameters;
    /**
     * This allows us to know the axis at runtime
     */
    private final AxisOrientation mAxisOrientation;
    /**
     * Minor GridLines
     */
    private final Map<Integer, GridLines> mChildGridLines = new ConcurrentHashMap<>();
    /**
     * Number of grid lines to display in the area
     */
    int mNumberOfGridLines = 6;
    /**
     * The axis text to be displayed if needed
     */
    AxisText mAxisText;
    /**
     * An offset in relation to drawable area 0-1
     */
    float mGridLinesOffset = 0;
    /**
     * scale for child grid lines
     */
    Scale mChildGridLineScale;
    /**
     * This is a zoom that is never changed over the runtime of the app. Useful for setting limits
     */
    private ZoomDisplay mFixedZoomDisplay;
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
    private final ZoomChangedListener mZoomChangeListener = new ZoomChangedListener() {
        @Override
        public void zoomChanged() {
            refreshChildGridLines();
        }
    };

    /**
     * Constructor of GridLines
     *
     * @param axisOrientation either the x or y axis
     * @param axisParameters  parameters of graph shown
     */
    GridLines(AxisOrientation axisOrientation, AxisParameters axisParameters) {
        this.mAxisOrientation = axisOrientation;
        this.mAxisParameters = axisParameters;

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

    /**
     * If axis text is to be show then the context is needed
     *
     * @param context application context
     */
    public void showAxisText(Context context) {
        mContext = context;
    }

    /**
     * Number of grid lines at 100% zoom
     *
     * @return current number of grid lines
     */
    int getNumberOfGridLines() {
        return mNumberOfGridLines;
    }

    /**
     * Set the number of Grid lines for this object
     *
     * @param numberOfGridLines amount of grid lines
     */
    void setNumberOfGridLines(int numberOfGridLines) {
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
    private void setColor(@SuppressWarnings("SameParameterValue") int color) {
        mPaint.setColor(color);
    }

    /**
     * @return width between grid lines
     */
    float getGridLineDrawableWidth() {
        // -1 as we want the first grid line to be at 0 and the last at the width of the graph
        return mGridLinesSize / (float) (mNumberOfGridLines - 1);
    }

    /**
     * to be overridden by implementation specific code
     *
     * @param gridLine a value between 0 and number of grid lines
     * @return the intersect between 0 - 1
     */
    abstract float intersect(int gridLine);

    /**
     * Gives the value of where a grid line will interest x on the screen
     *
     * @param gridLine        grid line to find, base 0
     * @return the x Intersect
     */
    float intersectZoomCompensated(int gridLine) {
        float intersect = intersect(gridLine);
        if(intersect == GRID_LINE_OUT_OF_RANGE) {
            return GRID_LINE_OUT_OF_RANGE;
        }

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

    /**
     * An offset applied to this set of grid lines
     *
     * @param graphOffset a value between 0 - 1
     */
    void setGridLinesOffset(float graphOffset) {
        mGridLinesOffset = graphOffset;
    }

    /**
     * Set child grid lines to log or lin
     *
     * @param scale log or lin
     */
    void setChildGridLineScale(Scale scale) {
        mChildGridLineScale = scale;
        refreshChildGridLines();
    }

    /**
     * As axis text has to attain the drawable area before the grid lines to ensure grid lines do
     * not over lap the text. We have to provide another function. this function has to be called
     * for each grid lines before {@code surfaceChanged} of grid lines
     *
     * @param drawableArea the drawable area available
     */
    void notifyAxisTextOfSurfaceChange(DrawableArea drawableArea) {
        if (mAxisText != null) {
            mAxisText.surfaceChanged(drawableArea);
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
        refreshChildGridLines();
    }

    /**
     * @return the zoom object attached to these grid lines
     */
    private ZoomDisplay getZoomDisplay() {
        return mZoomDisplay;
    }

    /**
     * Set the zoomDisplay for the grid lines should morph to
     *
     * @param zoomDisplay zoomDisplay to set
     */
    void setZoomDisplay(ZoomDisplay zoomDisplay) {
        mZoomDisplay = zoomDisplay;

        zoomDisplay.addListener(mZoomChangeListener);
    }

    /**
     * remove the zoomDisplay, this is done to remove the listener.
     */
    private void removeZoomDisplay() {
        mZoomDisplay.removeListener(mZoomChangeListener);
        mZoomDisplay = new ZoomDisplay(1f, 0f);
    }

    /**
     * Checks to see if there is still space for child grid lines and add/removes them where needed
     */
    private void refreshChildGridLines() {
        SparseBooleanArray minorXGridLinesToDisplay = adequateSpaceForMinorGridLines();

        final int arraySize = minorXGridLinesToDisplay.size();

        for(int i = 0; i < arraySize; i++) {
            int key = minorXGridLinesToDisplay.keyAt(i);
            if(minorXGridLinesToDisplay.get(key)) {
                if(!mChildGridLines.containsKey(key)) {
                    addMinorGridLine(key);
                }
            } else {
                GridLines childGridLine = mChildGridLines.remove(key);
                // We must ensure the observer is removed
                if(childGridLine != null) {
                    childGridLine.removeZoomDisplay();
                }
            }
        }
    }

    /**
     * Reports if there is adequate space to fit minor grid lines between current grid lines
     *
     * @return a key pair that gives a boolean to show if there is enough space between the grid
     * lines
     */
    private SparseBooleanArray adequateSpaceForMinorGridLines() {
        SparseBooleanArray adequateSpaceList = new SparseBooleanArray();
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
        int numberOfChildGridLines = 11;
        if (!mChildGridLines.containsKey(majorGridLine)) {
            GridLines minorGridLine;

            if(mChildGridLineScale == Scale.linear) {
                if(mAxisOrientation == AxisOrientation.xAxis) {
                    minorGridLine = new LinXGridLines(mAxisParameters);
                } else {
                    minorGridLine = new LinYGridLines(mAxisParameters);
                }
            } else {
                float gridLineMinimumValue = mAxisParameters.graphPositionToScaledAxis(
                        intersect(majorGridLine));

                float gridLineMaximumValue = mAxisParameters.graphPositionToScaledAxis(
                        intersect(majorGridLine + 1));

                // calculates the number of gridLines needed to give a equal whole number
                // spacing
                float removeTrailingZeros =
                        Math.round(gridLineMaximumValue - gridLineMinimumValue);
                while(removeTrailingZeros % 1 == 0) {
                    removeTrailingZeros /= 10f;
                }
                removeTrailingZeros *= 10;

                for(int i = 9; i > 0; i--) {
                    if((removeTrailingZeros / (float) i) % 1 > 0) {
                        numberOfChildGridLines = i + 2;
                        break;
                    }
                }

                if(mAxisOrientation == AxisOrientation.xAxis) {
                    minorGridLine = new LogXGridLines(mAxisParameters, gridLineMinimumValue,
                            gridLineMaximumValue);
                } else {
                    minorGridLine = new LogYGridLines(mAxisParameters, gridLineMinimumValue,
                            gridLineMaximumValue);
                }
            }
            minorGridLine.setGridStrokeWidth(2);
            minorGridLine.setColor(Color.DKGRAY);
            minorGridLine.setNumberOfGridLines(numberOfChildGridLines);
            minorGridLine.setFixedZoomDisplay(mFixedZoomDisplay);
            mChildGridLines.put(majorGridLine, minorGridLine);


            if (mAxisText != null) {
                minorGridLine.showAxisText(mContext);
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
        // remove fixed zoom offsets to get true relative intersects
        left *= getFixedZoomDisplay().getZoomLevelPercentage();
        left -= getFixedZoomDisplay().getDisplayOffsetPercentage();
        float right = intersect(majorGridLine + 1);
        // remove fixed zoom offsets to get true relative intersects
        right *= getFixedZoomDisplay().getZoomLevelPercentage();
        right -= getFixedZoomDisplay().getDisplayOffsetPercentage();

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
     * Remove all the child grid lines from this object
     */
    void removeAllChildGridLines() {
        for(Iterator<Map.Entry<Integer, GridLines>> it = mChildGridLines.entrySet().iterator();
            it.hasNext(); ) {
            Map.Entry<Integer, GridLines> entry = it.next();
            entry.getValue().removeAllChildGridLines();
            it.remove();
        }
    }

    /**
     * Used to report back the height for the yAxis or width on the xAxis
     *
     * @return dimension length
     */
    abstract float getDimensionLength();

    /**
     * @return axis text displayed which could be null
     */
    private AxisText getAxisText() {
        return mAxisText;
    }

    /**
     * @return the fixed zoom display applied to these grid lines
     */
    ZoomDisplay getFixedZoomDisplay() {
        return mFixedZoomDisplay;
    }

    /**
     * Set a zoom level which is fixed this and all child grid lines
     *
     * @param zoomDisplay zoom display object with fixed values
     */
    private void setFixedZoomDisplay(ZoomDisplay zoomDisplay) {
        this.mFixedZoomDisplay = zoomDisplay;
    }

    @Override
    public void setColour(int colour) {
        super.setColour(colour);
        if(mAxisText != null) {
            mAxisText.setColour(colour);
        }
    }

    enum AxisOrientation {
        xAxis,
        yAxis
    }
}
