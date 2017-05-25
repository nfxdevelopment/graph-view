package com.nfx.android.graph.androidgraph;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.nfx.android.graph.R;
import com.nfx.android.graph.androidgraph.AxisScale.GraphParameters;

/**
 * NFX Development
 * Created by nick on 25/10/15.
 * <p/>
 * The GraphManager handles or instructs the drawing of the graph. It will start displaying when
 * the surface is created and will only removeAllChildGridLines when the surface is destroyed. It
 * has the ability to
 * handle a resize at runtime.
 */
public class GraphView extends SurfaceView implements SurfaceHolder.Callback, GraphViewInterface {
    /**
     * Parameter limits for the graph shown
     */
    private final GraphParameters graphParameters = new GraphParameters();
    /**
     * Information about the scaling of signal in the x axis
     */
    private ZoomDisplay xZoomDisplay;
    /**
     * Information about the scaling of signal in the y axis
     */
    private ZoomDisplay yZoomDisplay;
    /**
     * An object to draw all of the background information, including grid lines, axis information
     * and a background color
     */
    private BackgroundManager backgroundManager;
    /**
     * Handles the drawing of a unlimited amount of Signals
     **/
    private SignalManager signalManager;
    /**
     * The thread that updates the surface
     **/
    private GraphManagerThread graphManagerThread;
    /**
     * GraphListManager that handles text boxes
     */
    private GraphListManager graphListManager;
    /**
     * Drawable area for whole Graph view
     */
    private DrawableArea drawableArea;

    /**
     * Constructor for graph manager
     *
     * @param context current application context
     */
    public GraphView(Context context, GraphListManager graphListManager) {
        super(context);
        this.graphListManager = graphListManager;
        initialise();
    }

    /**
     * @param context application context
     * @param attrs   attributes to be applied
     */
    public GraphView(Context context, AttributeSet attrs, GraphListManager graphListManager) {
        super(context, attrs);
        this.graphListManager = graphListManager;
        initialise(attrs);
    }

    /**
     * @param context      application context
     * @param attrs        attributes to be applied
     * @param defStyleAttr style attributes
     */
    public GraphView(Context context, AttributeSet attrs, int defStyleAttr,
                     GraphListManager graphListManager) {
        super(context, attrs, defStyleAttr);
        this.graphListManager = graphListManager;
        initialise(attrs);
    }

    public GraphView(Context context) {
        super(context);
        this.graphListManager = new GraphListManager(context, this);
        initialise(null);
    }

    /**
     * Generalized initialise method called only by the constructors where there is attributes to be
     * applied
     * @param attrs attributes to be applied
     */
    private void initialise(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.GraphManager,
                0, 0);

        try {
            graphParameters.getXAxisParameters().setMinimumValue(
                    a.getFloat(R.styleable.GraphManager_minimum_x_value, 0));
            graphParameters.getXAxisParameters().setMaximumValue(
                    a.getFloat(R.styleable.GraphManager_maximum_x_value, 1));
            graphParameters.getYAxisParameters().setMinimumValue(
                    a.getFloat(R.styleable.GraphManager_minimum_y_value, 0));
            graphParameters.getYAxisParameters().setMaximumValue(
                    a.getFloat(R.styleable.GraphManager_maximum_y_value, 1));

            initialise();

            backgroundManager.setShowAxisText(
                    a.getBoolean(R.styleable.GraphManager_show_axis_text, true));

            backgroundManager.setBackgroundColour(
                    a.getColor(R.styleable.GraphManager_background_color,
                            ContextCompat.getColor(getContext(), R.color.background)));


            if(a.getBoolean(R.styleable.GraphManager_limit_x_axis_offset, true)) {
                xZoomDisplay = new ZoomDisplayWithOffsetBounds(1f, 0f);
            }

            if(a.getBoolean(R.styleable.GraphManager_limit_y_axis_offset, true)) {
                yZoomDisplay = new ZoomDisplayWithOffsetBounds(1f, 0f);
            }

            if(!a.getBoolean(R.styleable.GraphManager_disable_background_scrolling, false)) {
                backgroundManager.setXZoomDisplay(xZoomDisplay);
                backgroundManager.setYZoomDisplay(yZoomDisplay);
            }

        } finally {
            a.recycle();
        }
    }

    /**
     * Local object setup
     */
    private void initialise() {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        graphManagerThread = new GraphManagerThread();
        backgroundManager = new BackgroundManager(getContext(), graphParameters);
        signalManager = new SignalManager(this, graphListManager);


        if(graphParameters.getXAxisParameters().getAxisScale() == Scale.logarithmic) {
            setXAxisLogarithmic();
        } else {
            setXAxisLinear();
        }

        xZoomDisplay = new ZoomDisplay(1f, 0f);
        yZoomDisplay = new ZoomDisplay(1f, 0f);
    }

    /**
     * The graphManager thread is started in here which will cause the surface view to start
     * displaying
     *
     * @param holder the current surface holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    /**
     * Any time a change in size is seen this is called, this information is used to manipulate
     * any drawable areas graph manager knows of
     *
     * @param holder the surface holder that has changed
     * @param format the new pixel format
     * @param width the new width
     * @param height the new height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        drawableArea = new DrawableArea(0, 0, width, height);
        backgroundManager.surfaceChanged(drawableArea);
        signalManager.surfaceChanged(drawableArea);
    }

    /**
     * Called when surface is destroyed. Here we kill the thread cleanly
     *
     * @param holder the surface holder that has been destroyed
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stop();
    }

    /**
     * Start displaying the graph output on screen
     */
    public void start() {
        graphManagerThread = new GraphManagerThread();
        graphManagerThread.setRun(true);
        graphManagerThread.start();
    }

    /**
     * Stop updating the screen and remove signal listeners
     */
    public void stop() {
        backgroundManager.removeAllChildGridLines();

        boolean retry = true;
        if(graphManagerThread != null) {
            graphManagerThread.setRun(false);
            while (retry) {
                try {
                    graphManagerThread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            graphManagerThread = null;
        }
    }

    /**
     * Calls through to children draw methods which fills the canvas with current graphic data
     *
     * @param canvas canvas to draw on
     */
    private void doDraw(Canvas canvas) {
        backgroundManager.doDraw(canvas);
        signalManager.doDraw(canvas);
    }

    /**
     * @return the manager for background drawing
     */
    public BackgroundManagerInterface getBackgroundManager() {
        return backgroundManager;
    }

    /**
     * @return the manager for drawing signals
     */
    public SignalManagerInterface getSignalManager() {
        return signalManager;
    }

    /**
     * Change the graphView to display a Logarithmic X axis
     */
    public void setXAxisLogarithmic() {
        graphParameters.getXAxisParameters().setAxisScale(Scale.logarithmic);

        backgroundManager.setXAxisLogarithmic();
    }

    /**
     * Change the graphView to display a Linear X axis
     */
    public void setXAxisLinear() {
        graphParameters.getXAxisParameters().setAxisScale(Scale.linear);

        backgroundManager.setXAxisLinear();
    }

    @Override
    public ZoomDisplay getGraphXZoomDisplay() {
        return xZoomDisplay;
    }

    @Override
    public ZoomDisplay getGraphYZoomDisplay() {
        return yZoomDisplay;
    }

    /**
     * @return parameters and limits of the current graph
     */
    public GraphParameters getGraphParameters() {
        return graphParameters;
    }

    @Override
    public DrawableArea getDrawableArea() {
        return drawableArea;
    }

    /**
     * @return the zoom scaling for the x axis
     */
    public ZoomDisplay getXZoomDisplay() {
        return xZoomDisplay;
    }

    /**
     * @return the zoom scaling for the y axis
     */
    public ZoomDisplay getYZoomDisplay() {
        return yZoomDisplay;
    }

    /**
     * The drawing thread for the graph data
     */
    private class GraphManagerThread extends Thread {
        /**
         * Screen refresh Rate in milliseconds
         */
        private final static long SCREEN_REFRESH_RATE = 17;
        /**
         * Indicate whether the surface has been created & is ready to draw
         */
        private boolean run = false;

        GraphManagerThread() {
            setFocusable(true);
        }

        @Override
        public void run() {
            while(run) {
                long startTime = (System.currentTimeMillis());
                Canvas canvas = null;
                final SurfaceHolder surfaceHolder = getHolder();
                try {
                    canvas = surfaceHolder.lockCanvas(null);
                    if (canvas != null) {
                        doDraw(canvas);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                long endTime = System.currentTimeMillis();
                long sleepTime = SCREEN_REFRESH_RATE - (endTime - startTime);
                if(sleepTime < 0) {
                    sleepTime = 1;
                }

                try {
                    sleep(sleepTime, 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * This will allow the thread to run. If it is set to false the thread will die until
         * run is set to true again and start() is called.
         *
         * @param run true to run, false to shut down
         */
        void setRun(boolean run) {
            this.run = run;
        }

    }
}
