package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import com.nfx.android.library.androidgraph.ZoomDisplay.ZoomChangedListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NFX Development
 * Created by nick on 28/10/15.
 *
 * The background manager holders many drawable objects which are considered background objects
 * It makes batch calls to DoDraw functions of all it's members and individual sizing options
 * are possible by overriding surfaceChanged
 */
public class BackgroundManager {
    private static final String TAG = "BackgroundManager";
    /**
     * An object which draws onto the canvas
     **/
    private Background mBackground;
    /**
     * An object to draw a board around the graph
     */
    private Boarder mBoarder;
    /**
     * Handles the drawing of all grid lines
     */
    private GridLines mYMajorGridLines;
    private GridLines mXMajorGridLines;
    private Map<Integer, GridLines> mXMinorGridLines = new ConcurrentHashMap<>();
    private Map<Integer, GridLines> mYMinorGridLines = new ConcurrentHashMap<>();
    /**
     * Handles the drawing of all text on axis
     */
    private Collection<AxisText> mXAxisText = new ArrayList<>();
    private Collection<AxisText> mYAxisText = new ArrayList<>();
    private BoarderText mBoarderText;
    /**
     * Set dependant which constuctor is called
     */
    private boolean mShowAxisText = false;

    /**
     * Constructor for Background Manager, all drawable objects are created here. Call this
     * constructor if you want the axis text to be shown
     *
     * @param context       application context
     * @param minimumXValue minimum value graph represents for x
     * @param maximumXValue maximum value graph represents for x
     * @param minimumYValue minimum value graph represents for y
     * @param maximumYValue maximum value graph represents for y
     */
    public BackgroundManager(Context context, float minimumXValue, float maximumXValue,
                             float minimumYValue, float maximumYValue) {
        this(context);

        mXAxisText.add(new XAxisText(context, mXMajorGridLines, minimumXValue, maximumXValue));
        mYAxisText.add(new YAxisText(context, mYMajorGridLines, minimumYValue, maximumYValue));
        mBoarderText = new BoarderText(context, minimumXValue, maximumXValue, minimumYValue,
                maximumYValue);
        mShowAxisText = true;
    }

    /**
     * Constructor for Background Manager, all drawable objects are created here
     * @param context application context
     */
    public BackgroundManager(Context context) {
        mBackground = new Background();
        mBoarder = new Boarder();

        mXMajorGridLines = new LinXGridLines();
        mYMajorGridLines = new LinYGridLines();
    }

    /**
     * Call when the surface view changes it's dimensions the objects have to called in the correct
     * order to ensure they take up the correct space
     *
     * @param drawableArea the avaiable area to draw
     * @return the area of which is stil drawable
     */
    public DrawableArea surfaceChanged(DrawableArea drawableArea) {
        mBackground.surfaceChanged(drawableArea);

        if (mShowAxisText) {
            mBoarderText.surfaceChanged(drawableArea);

            for (AxisText axisText : mYAxisText) {
                axisText.surfaceChanged(drawableArea);
            }
            for (AxisText axisText : mXAxisText) {
                axisText.surfaceChanged(drawableArea);
            }
        }

        mBoarder.surfaceChanged(drawableArea);

        // When the boarder is used we have to shift the axis text as it would no longer be inline
        // with the grid lines
        if (mShowAxisText) {
            for (AxisText axisText : mYAxisText) {
                axisText.getDrawableArea().setDrawableArea(axisText.getDrawableArea().getLeft(),
                        axisText.getDrawableArea().getTop() + mBoarder.getStrokeWidth(),
                        axisText.getDrawableArea().getWidth(),
                        axisText.getDrawableArea().getHeight() - mBoarder.getStrokeWidth());
            }
            for (AxisText axisText : mXAxisText) {
                axisText.getDrawableArea().setDrawableArea(
                        axisText.getDrawableArea().getLeft() + mBoarder.getStrokeWidth(),
                        axisText.getDrawableArea().getTop(),
                        axisText.getDrawableArea().getWidth() - mBoarder.getStrokeWidth(),
                        axisText.getDrawableArea().getHeight());
            }
        }

        mXMajorGridLines.surfaceChanged(drawableArea);
        mYMajorGridLines.surfaceChanged(drawableArea);
        for (Map.Entry<Integer, GridLines> minorGridLines : mXMinorGridLines.entrySet()) {
            minorGridLineSurfaceChanged(mXMajorGridLines, minorGridLines.getValue(),
                    minorGridLines.getKey());
        }
        for (Map.Entry<Integer, GridLines> minorGridLines : mYMinorGridLines.entrySet()) {
            minorGridLineSurfaceChanged(mYMajorGridLines, minorGridLines.getValue(),
                    minorGridLines.getKey());
        }

        return drawableArea;
    }

    /**
     * Call with the canvas to draw on
     * @param canvas canvas to draw the objects onto
     */
    public void doDraw(Canvas canvas) {
        mBackground.doDraw(canvas);
        mBoarder.doDraw(canvas);

        mXMajorGridLines.doDraw(canvas);
        mYMajorGridLines.doDraw(canvas);

        for (GridLines gridLines : mXMinorGridLines.values()) {
            gridLines.doDraw(canvas);
        }
        for (GridLines gridLines : mYMinorGridLines.values()) {
            gridLines.doDraw(canvas);
        }

        if (mShowAxisText) {
            for (AxisText axisText : mXAxisText) {
                axisText.doDraw(canvas);
            }
            for (AxisText axisText : mYAxisText) {
                axisText.doDraw(canvas);
            }
            mBoarderText.doDraw(canvas);
        }
    }

//    public GridLines getXMajorGridLines() {
//        return mXMajorGridLines;
//    }
//
//    public GridLines getYMajorGridLines() {
//        return mYMajorGridLines;
//    }

    /**
     * This tells the graph that there are signals to display, each signal gets its own drawer,
     * At the current time the last signal in the list will control the zoom levels. This is because
     * we are trying to control a single axis zoom from multiple signals. TODO
     *
     * @param signalBuffers pass the object of signals to display on the graph
     */
    public void setSignalBuffers(SignalBuffers signalBuffers) {
        for (SignalBuffer signalBuffer : signalBuffers.getSignalBuffer().values()) {

            mXMajorGridLines.setZoomDisplay(signalBuffer.getXZoomDisplay());
            mYMajorGridLines.setZoomDisplay(signalBuffer.getYZoomDisplay());

            mBoarderText.setZoomDisplay(signalBuffer.getXZoomDisplay(),
                    signalBuffer.getYZoomDisplay());

            signalBuffer.getXZoomDisplay().setTheListener(new ZoomChangedListener() {

                @Override
                public void zoomChanged() {
                    Map<Integer, Boolean> minorXGridLinesToDisplay =
                            mXMajorGridLines.adequateSpaceForMinorGridLines();

                    for (Map.Entry<Integer, Boolean> majorGridLine : minorXGridLinesToDisplay
                            .entrySet()) {
                        if (majorGridLine.getValue() == true) {
                            addXMinorGridLines(mXMajorGridLines, mXMinorGridLines, majorGridLine
                                    .getKey());
                        } else {
                            mXMinorGridLines.remove(majorGridLine.getKey());
                        }
                    }
                }
            });
            signalBuffer.getYZoomDisplay().setTheListener(new ZoomChangedListener() {

                @Override
                public void zoomChanged() {
                    Map<Integer, Boolean> minorYGridLinesToDisplay =
                            mYMajorGridLines.adequateSpaceForMinorGridLines();

                    for (Map.Entry<Integer, Boolean> majorGridLine : minorYGridLinesToDisplay
                            .entrySet()) {
                        if (majorGridLine.getValue() == true) {
                            addYMinorGridLines(mYMajorGridLines, mYMinorGridLines, majorGridLine
                                    .getKey());
                        } else {
                            mYMinorGridLines.remove(majorGridLine.getKey());
                        }
                    }
                }
            });
        }
    }

    void addXMinorGridLines(GridLines parentGridLines, Map<Integer, GridLines> childGridLines,
                            int majorGridLine) {
        if (!childGridLines.containsKey(majorGridLine)) {
            GridLines minorGridLines = new LinXGridLines();
            minorGridLines.setGridStrokeWidth(2);
            minorGridLines.setColor(Color.DKGRAY);
            childGridLines.put(majorGridLine, minorGridLines);
            minorGridLines.setZoomDisplay(parentGridLines.getZoomDisplay());

            minorGridLineSurfaceChanged(parentGridLines, minorGridLines, majorGridLine);
        }
    }

    void addYMinorGridLines(GridLines parentGridLines, Map<Integer, GridLines> childGridLines,
                            int majorGridLine) {
        GridLines minorGridLines = new LinYGridLines();
        minorGridLines.setGridStrokeWidth(2);
        minorGridLines.setColor(Color.DKGRAY);
        childGridLines.put(majorGridLine, minorGridLines);
        minorGridLines.setZoomDisplay(parentGridLines.getZoomDisplay());

        minorGridLineSurfaceChanged(parentGridLines, minorGridLines, majorGridLine);
    }

    void minorGridLineSurfaceChanged(GridLines parentGridLines, GridLines childGridLines,
                                     int majorGridLine) {
        DrawableArea parentDrawableArea = parentGridLines.getDrawableArea();

        int left = (int) parentGridLines.intersect(majorGridLine - 1);
        int right = (int) parentGridLines.intersect(majorGridLine);
        if (majorGridLine == 0) {
            left = 0;
        } else if (majorGridLine == parentGridLines.getNumberOfGridLines()) {
            right = parentDrawableArea.getWidth();
        }
        childGridLines.surfaceChanged(parentDrawableArea);

        childGridLines.setGridLinesSize(right - left);
        childGridLines.setGridLinesOffset(left);
    }
}
