package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;

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
    private final Background mBackground;
    /**
     * An object to draw a board around the graph
     */
    private final Boarder mBoarder;
    /**
     * Handles the drawing of all grid lines
     */
    private final GridLines mYMajorGridLines;
    private final GridLines mXMajorGridLines;
    /**
     * Handles the drawing of all text on axis
     */
    private BoarderText mBoarderText;
    /**
     * Set dependant which constructor is called
     */
    private boolean mShowAxisText = false;
    /**
     * Minimum and Maximum values of the axis
     */
    private float mMinimumXValue;
    private float mMaximumXValue;
    private float mMinimumYValue;
    private float mMaximumYValue;


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

        this();
        mShowAxisText = true;
        mBoarderText = new BoarderText(context, minimumXValue, maximumXValue, minimumYValue,
                maximumYValue);
        mXMajorGridLines.showAxisText(context, minimumXValue, maximumXValue);
        mYMajorGridLines.showAxisText(context, minimumYValue, maximumYValue);
    }

    /**
     * Constructor for Background Manager, all drawable objects are created here
     */
    public BackgroundManager() {
        mBackground = new Background();
        mBoarder = new Boarder();

        mXMajorGridLines = new LinXGridLines();
        mYMajorGridLines = new LinYGridLines();
    }

    /**
     * Call when the surface view changes it's dimensions the objects have to called in the correct
     * order to ensure they take up the correct space
     *
     * @param drawableArea the available area to draw
     */
    public void surfaceChanged(DrawableArea drawableArea) {
        mBackground.surfaceChanged(drawableArea);

        if (mShowAxisText) {
            mBoarderText.surfaceChanged(drawableArea);
            // we have to call Y here first to shift the x text into the right location
            mYMajorGridLines.notifyAxisTextOfSurfaceChange(drawableArea);
            mXMajorGridLines.notifyAxisTextOfSurfaceChange(drawableArea);
            mYMajorGridLines.getAxisText().setGraphBoarderSize(mBoarder.getStrokeWidth());
            mXMajorGridLines.getAxisText().setGraphBoarderSize(mBoarder.getStrokeWidth());
        }

        mBoarder.surfaceChanged(drawableArea);

        mXMajorGridLines.surfaceChanged(drawableArea);
        mYMajorGridLines.surfaceChanged(drawableArea);
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

        if (mShowAxisText) {
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
        }
    }
}
