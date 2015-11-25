package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collection;

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
    private Collection<GridLines> mGridLinesMinor = new ArrayList<>();
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

        mGridLinesMinor.add(new LogXGridLines());

        for (GridLines gridLines : mGridLinesMinor) {
            gridLines.setGridStrokeWidth(2);
            gridLines.setColor(Color.DKGRAY);
        }
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

        for (GridLines gridLinesMinor : mGridLinesMinor) {
            if (gridLinesMinor.getAxisOrientation() == GridLines.AxisOrientation.xAxis) {
                int minorGridLineWidth = (int) mXMajorGridLines.intersect(0);
                gridLinesMinor.getDrawableArea().setDrawableArea(drawableArea.getLeft(),
                        drawableArea.getTop(), minorGridLineWidth, drawableArea.getHeight());
            }
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

        for (GridLines gridLines : mGridLinesMinor) {
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

    public GridLines getXMajorGridLines() {
        return mXMajorGridLines;
    }

    public GridLines getYMajorGridLines() {
        return mYMajorGridLines;
    }

    /**
     * This tells the graph that there are signals to display, each signal gets its own drawer,
     * At the current time the last signal in the list will control the zoom levels. This is because
     * we are trying to control a single axis zoom from multiple signals. TODO
     *
     * @param signalBuffers pass the object of signals to display on the graph
     */
    public void setSignalBuffers(SignalBuffers signalBuffers) {
        for (SignalBuffer signalBuffer : signalBuffers.getSignalBuffer().values()) {

            getXMajorGridLines().setZoomDisplay(signalBuffer.getXZoomDisplay());
            getYMajorGridLines().setZoomDisplay(signalBuffer.getYZoomDisplay());

            mBoarderText.setZoomDisplay(signalBuffer.getXZoomDisplay(),
                    signalBuffer.getYZoomDisplay());
        }
    }
}
