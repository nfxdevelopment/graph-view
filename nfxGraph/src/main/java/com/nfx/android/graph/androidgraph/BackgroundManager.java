package com.nfx.android.graph.androidgraph;

import android.content.Context;
import android.graphics.Canvas;

import com.nfx.android.graph.androidgraph.AxisScale.GraphParameters;

/**
 * NFX Development
 * Created by nick on 28/10/15.
 * <p/>
 * The background manager holders many drawable objects which are considered background objects
 * It makes batch calls to DoDraw functions of all it's members and individual sizing options
 * are possible by overriding surfaceChanged
 */
public class BackgroundManager {
    /**
     * An object which draws onto the canvas
     **/
    private final Background background;
    /**
     * An object to draw a board around the graph
     */
    private final Boarder boarder;
    /**
     * Handles the drawing of all grid lines
     */
    private GridLines yGridLines;
    private GridLines xGridLines;

    /**
     * Handles the drawing of all text on axis
     */
    private BoarderText boarderText;
    /**
     * Set dependant which constructor is called
     */
    private boolean showAxisText = false;
    /**
     * Graph Limits
     */
    private GraphParameters graphParameters;
    /**
     * App Context
     */
    private Context context;


    /**
     * Constructor for Background Manager, all drawable objects are created here. Call this
     * constructor if you want the axis text to be shown
     *
     * @param context         application context
     * @param graphParameters An object holding the graph limits
     */
    BackgroundManager(Context context, GraphParameters graphParameters) {
        this();
        this.graphParameters = graphParameters;
        this.context = context;
        boarderText = new BoarderText(context, graphParameters);
        xGridLines = new LinXGridLines(graphParameters.getXAxisParameters());
        yGridLines = new LinYGridLines(graphParameters.getYAxisParameters());
    }

    /**
     * Constructor for Background Manager, all drawable objects are created here
     */
    @SuppressWarnings("WeakerAccess")
    public BackgroundManager() {
        background = new Background();
        boarder = new Boarder();
    }

    /**
     * Call when the surface view changes it's dimensions the objects have to called in the correct
     * order to ensure they take up the correct space
     *
     * @param drawableArea the available area to draw
     */
    public void surfaceChanged(DrawableArea drawableArea) {
        background.surfaceChanged(drawableArea);

        if(showAxisText) {
            boarderText.surfaceChanged(drawableArea);
            // we have to call Y here first to shift the x text into the right location
            yGridLines.notifyAxisTextOfSurfaceChange(drawableArea);
            xGridLines.notifyAxisTextOfSurfaceChange(drawableArea);
        }

        boarder.surfaceChanged(drawableArea);

        xGridLines.surfaceChanged(drawableArea);
        yGridLines.surfaceChanged(drawableArea);
    }

    /**
     * Call with the canvas to draw on
     * @param canvas canvas to draw the objects onto
     */
    public void doDraw(Canvas canvas) {
        background.doDraw(canvas);
        boarder.doDraw(canvas);

        xGridLines.doDraw(canvas);
        yGridLines.doDraw(canvas);

        if(showAxisText) {
            boarderText.doDraw(canvas);
        }
    }

    /**
     * Sets the zoom Display object for the background objects to use and listen to
     *
     * @param yZoomDisplay zoom object for the y axis
     */
    void setYZoomDisplay(ZoomDisplay yZoomDisplay) {
        yGridLines.setZoomDisplay(yZoomDisplay);
        boarderText.setYZoomDisplay(yZoomDisplay);
    }

    /**
     * Sets the zoom Display object for the background objects to use and listen to
     *
     * @param zoomDisplay zoom object for the x axis
     */
    void setXZoomDisplay(ZoomDisplay zoomDisplay) {
        xGridLines.setZoomDisplay(zoomDisplay);
        boarderText.setXZoomDisplay(zoomDisplay);
    }

    /**
     * Handle changes to display the x axis as a logarithmic scale
     */
    void setXAxisLogarithmic() {
        // We want the decades located on the major grid lines but want the max to be 22.05K
        // therefore we have to do a fudge to make 100K the maximum and zoom to 0 - 22.05K
        float max = graphParameters.getXAxisParameters().getMaximumValue();
        int i = 0;
        while(max >= 1) {
            i++;
            max /= 10;
        }
        float newMax = (float) Math.pow(10, i);

        xGridLines.setNumberOfGridLines(i);
        xGridLines.getFixedZoomDisplay().setZoomLevelPercentage(1f /
                graphParameters.getXAxisParameters().scaledAxisToGraphPosition(newMax));

        // Axis are reset so lets remove all the children
        xGridLines.removeAllChildGridLines();
        yGridLines.removeAllChildGridLines();

        xGridLines.setChildGridLineScale(Scale.logarithmic);
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
        while((graphParameters.getXAxisParameters().getMaximumValue() / divisor) > 1f) {
            divisor += iterator;
            i++;
            if(divisor >= (iterator * 10)) {
                iterator *= 10f;
                divisor = iterator;
                i = 1;
            }
        }

        xGridLines.getFixedZoomDisplay().setZoomLevelPercentage(
                graphParameters.getXAxisParameters().getMaximumValue() / divisor);

        // Axis are reset so lets remove all the children
        xGridLines.removeAllChildGridLines();
        yGridLines.removeAllChildGridLines();

        xGridLines.setChildGridLineScale(Scale.linear);
    }

    /**
     * Set the colour of the background
     *
     * @param colour colour to set
     */
    void setBackgroundColour(int colour) {
        background.setColour(colour);
    }

    /**
     * Set the colour of the grid lines
     *
     * @param colour colour to set
     */
    void setGridLineColour(int colour) {
        xGridLines.setColour(colour);
        yGridLines.setColour(colour);
        boarder.setColour(colour);
        boarderText.setColour(colour);
    }

    /**
     * Remove child grid lines for x and y axis
     */
    void removeAllChildGridLines() {
        xGridLines.removeAllChildGridLines();
        yGridLines.removeAllChildGridLines();
    }


    void setShowAxisText(boolean showAxisText) {
        this.showAxisText = showAxisText;

        xGridLines.showAxisText(context);
        yGridLines.showAxisText(context);
    }

    public void forceGridLineValueRecalculate() {
        boarderText.calculateValuesToDisplay();
        yGridLines.axisText.calculateGridLineValues();
        xGridLines.axisText.calculateGridLineValues();
    }

    public GridLines getXGridLines() {
        return xGridLines;
    }

    public GridLines getYGridLines() {
        return yGridLines;
    }

}
