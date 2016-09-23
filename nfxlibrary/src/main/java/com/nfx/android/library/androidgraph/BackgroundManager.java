package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;

import com.nfx.android.library.androidgraph.AxisScale.GraphParameters;

/**
 * NFX Development
 * Created by nick on 28/10/15.
 * <p/>
 * The background manager holders many drawable objects which are considered background objects
 * It makes batch calls to DoDraw functions of all it's members and individual sizing options
 * are possible by overriding surfaceChanged
 */
class BackgroundManager {
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
    private GridLines mYGridLines;
    private GridLines mXGridLines;

    /**
     * Handles the drawing of all text on axis
     */
    private BoarderText mBoarderText;
    /**
     * Set dependant which constructor is called
     */
    private boolean mShowAxisText = false;
    /**
     * Graph Limits
     */
    private GraphParameters mGraphParameters;


    /**
     * Constructor for Background Manager, all drawable objects are created here. Call this
     * constructor if you want the axis text to be shown
     *
     * @param context         application context
     * @param graphParameters An object holding the graph limits
     */
    BackgroundManager(Context context, GraphParameters graphParameters) {
        this();
        this.mGraphParameters = graphParameters;
        mShowAxisText = true;
        mBoarderText = new BoarderText(context, graphParameters);
        mXGridLines = new LinXGridLines(graphParameters.getXAxisParameters());
        mYGridLines = new LinYGridLines(graphParameters.getYAxisParameters());
        mXGridLines.showAxisText(context);
        mYGridLines.showAxisText(context);
    }

    /**
     * Constructor for Background Manager, all drawable objects are created here
     */
    @SuppressWarnings("WeakerAccess")
    public BackgroundManager() {
        mBackground = new Background();
        mBoarder = new Boarder();
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
            mYGridLines.notifyAxisTextOfSurfaceChange(drawableArea);
            mXGridLines.notifyAxisTextOfSurfaceChange(drawableArea);
        }

        mBoarder.surfaceChanged(drawableArea);

        mXGridLines.surfaceChanged(drawableArea);
        mYGridLines.surfaceChanged(drawableArea);
    }

    /**
     * Call with the canvas to draw on
     * @param canvas canvas to draw the objects onto
     */
    public void doDraw(Canvas canvas) {
        mBackground.doDraw(canvas);
        mBoarder.doDraw(canvas);

        mXGridLines.doDraw(canvas);
        mYGridLines.doDraw(canvas);

        if (mShowAxisText) {
            mBoarderText.doDraw(canvas);
        }
    }

    /**
     * Sets the zoom Display object for the background objects to use and listen to
     *
     * @param yZoomDisplay zoom object for the y axis
     */
    void setYZoomDisplay(ZoomDisplay yZoomDisplay) {
        mYGridLines.setZoomDisplay(yZoomDisplay);
        mBoarderText.setYZoomDisplay(yZoomDisplay);
    }

    /**
     * Sets the zoom Display object for the background objects to use and listen to
     *
     * @param zoomDisplay zoom object for the x axis
     */
    void setXZoomDisplay(ZoomDisplay zoomDisplay) {
        mXGridLines.setZoomDisplay(zoomDisplay);
        mBoarderText.setXZoomDisplay(zoomDisplay);
    }

    /**
     * Handle changes to display the x axis as a logarithmic scale
     */
    void setXAxisLogarithmic() {
        // We want the decades located on the major grid lines but want the max to be 22.05K
        // therefore we have to do a fudge to make 100K the maximum and zoom to 0 - 22.05K
        float max = mGraphParameters.getXAxisParameters().getMaximumValue();
        int i = 0;
        while(max >= 1) {
            i++;
            max /= 10;
        }
        float newMax = (float) Math.pow(10, i);

        mXGridLines.setNumberOfGridLines(i);
        mXGridLines.getFixedZoomDisplay().setZoomLevelPercentage(1f /
                mGraphParameters.getXAxisParameters().scaledAxisToGraphPosition(newMax));

        // Axis are reset so lets remove all the children
        mXGridLines.removeAllChildGridLines();
        mYGridLines.removeAllChildGridLines();

        mXGridLines.setChildGridLineScale(Scale.logarithmic);
    }

    /**
     * Handle changes to display the x axis as a linear scale
     */
    void setXAxisLinear() {
        // This will scale the x axis in a logical fashion. it will round up to a value based on
        // the iterator. The iterator will grown by a factor of 10 after each 10 iterations round
        // round the loop. results eg. 22500 = 30000 | 0.05 = 0.1
        float iterator = 1f;
        float divisor = iterator;
        int i = 1;
        while((mGraphParameters.getXAxisParameters().getMaximumValue() / divisor) > 1f) {
            divisor += iterator;
            i++;
            if(divisor >= (iterator * 10)) {
                iterator *= 10f;
                divisor = iterator;
                i = 1;
            }
        }

        mXGridLines.getFixedZoomDisplay().setZoomLevelPercentage(
                mGraphParameters.getXAxisParameters().getMaximumValue() / divisor);

        // Axis are reset so lets remove all the children
        mXGridLines.removeAllChildGridLines();
        mYGridLines.removeAllChildGridLines();

        mXGridLines.setChildGridLineScale(Scale.linear);
    }

    /**
     * Set the colour of the background
     *
     * @param colour colour to set
     */
    void setBackgroundColour(int colour) {
        mBackground.setColour(colour);
    }

    /**
     * Set the colour of the grid lines
     *
     * @param colour colour to set
     */
    void setGridLineColour(int colour) {
        mXGridLines.setColour(colour);
        mYGridLines.setColour(colour);
        mBoarder.setColour(colour);
        mBoarderText.setColour(colour);
    }

    /**
     * Remove child grid lines for x and y axis
     */
    void removeAllChildGridLines() {
        mXGridLines.removeAllChildGridLines();
        mYGridLines.removeAllChildGridLines();
    }
}
