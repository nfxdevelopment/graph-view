package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.nfx.android.library.R;
import com.nfx.android.library.androidgraph.AxisScale.AxisParameters;
import com.nfx.android.library.androidgraph.AxisScale.GraphParameters;
import com.nfx.android.library.graphbufferinput.InputListener;

/**
 * NFX Development
 * Created by nick on 25/10/15.
 * <p/>
 * The GraphManager handles or instructs the drawing of the graph. It will start displaying when
 * the surface is created and will only stop when the surface is destroyed. It has the ability to
 * handle a resize at runtime.
 */
public class GraphView extends SurfaceView implements SurfaceHolder.Callback {
    @SuppressWarnings("unused")
    private static final String TAG = "GraphManager";
    /**
     * An interface to update the signal data
     */
    private final GraphSignalInputInterface mGraphSignalInputInterface = new
            GraphSignalInputInterface();
    /**
     * Information about the scaling of signal in the x axis
     */
    public ZoomDisplay mXZoomDisplay;
    /**
     * Information about the scaling of signal in the y axis
     */
    public ZoomDisplay mYZoomDisplay;
    /**
     * An object to draw all of the background information, including grid lines, axis information
     * and a background color
     */
    private BackgroundManager mBackgroundManager;
    /**
     * Handles the drawing of a unlimited amount of Signals
     **/
    private SignalManager mSignalManager;
    /**
     * The thread that updates the surface
     **/
    private GraphManagerThread mGraphManagerThread;
    /**
     * Reference to the values limit for the shown graph
     */
    private GraphParameters mGraphParameters = new GraphParameters();

    /**
     * Constructor for graph manager
     *
     * @param context current application context
     */
    public GraphView(Context context) {
        super(context);
        initialise();
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise(attrs);
    }

    public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise(attrs);
    }

    private void initialise(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.GraphManager,
                0, 0);

        try {
            mGraphParameters.getXAxisParameters().setMinimumValue(
                    a.getFloat(R.styleable.GraphManager_minimum_x_value, 0));
            mGraphParameters.getXAxisParameters().setMaximumValue(
                    a.getFloat(R.styleable.GraphManager_maximum_x_value, 1));
            mGraphParameters.getYAxisParameters().setMinimumValue(
                    a.getFloat(R.styleable.GraphManager_minimum_y_value, 0));
            mGraphParameters.getYAxisParameters().setMaximumValue(
                    a.getFloat(R.styleable.GraphManager_maximum_y_value, 1));
        } finally {
            a.recycle();
        }
        initialise();
    }

    private void initialise() {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        mGraphManagerThread = new GraphManagerThread();
        mBackgroundManager = new BackgroundManager(getContext(), mGraphParameters);
        mSignalManager = new SignalManager(this);


        if(mGraphParameters.getXAxisParameters().getAxisScale() == Scale.logarithmic) {
            setXAxisLogarithmic();
        } else {
            setXAxisLinear();
        }

        mXZoomDisplay = new ZoomDisplay(1f, 0f);
        mBackgroundManager.setXZoomDisplay(mXZoomDisplay);
        mYZoomDisplay = new ZoomDisplay(1f, 0f);
        mBackgroundManager.setYZoomDisplay(mYZoomDisplay);
    }

    /**
     * Returns the signal input interface for the graph manager
     *
     * @return GraphSignalInputInterface object
     */
    public GraphSignalInputInterface getGraphSignalInputInterface() {
        return mGraphSignalInputInterface;
    }

    /**
     * The graphManager thread is started in here which will cause the surface view to start
     * displaying
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
        DrawableArea drawableArea = new DrawableArea(0, 0, width, height);
        mBackgroundManager.surfaceChanged(drawableArea);
        mSignalManager.surfaceChanged(drawableArea);
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
        mGraphManagerThread = new GraphManagerThread();
        mGraphManagerThread.setRun(true);
        mGraphManagerThread.start();
    }

    /**
     * Stop updating the screen and remove signal listeners
     */
    public void stop() {
        mBackgroundManager.stop();

        boolean retry = true;
        if (mGraphManagerThread != null) {
            mGraphManagerThread.setRun(false);
            while (retry) {
                try {
                    mGraphManagerThread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            mGraphManagerThread = null;
        }
    }

    /**
     * Calls through to children draw methods which fills the canvas with current graphic data
     *
     * @param canvas canvas to draw on
     */
    private void doDraw(Canvas canvas) {
        mBackgroundManager.doDraw(canvas);
        mSignalManager.doDraw(canvas);
    }

    public BackgroundManager getBackgroundManager() {
        return mBackgroundManager;
    }

    @SuppressWarnings("WeakerAccess")
    public SignalManager getSignalManager() {
        return mSignalManager;
    }

    /**
     * Change the graphView to display a Logarithmic X axis
     */
    public void setXAxisLogarithmic() {
        mGraphParameters.getXAxisParameters().setAxisScale(Scale.logarithmic);

        mBackgroundManager.setXAxisLogarithmic();
    }

    /**
     * Change the graphView to display a Linear X axis
     */
    public void setXAxisLinear() {
        mGraphParameters.getXAxisParameters().setAxisScale(Scale.linear);

        mBackgroundManager.setXAxisLinear();
    }

    public GraphParameters getGraphParameters() {
        return mGraphParameters;
    }

    public ZoomDisplay getXZoomDisplay() {
        return mXZoomDisplay;
    }

    public ZoomDisplay getYZoomDisplay() {
        return mYZoomDisplay;
    }

    /**
     * The drawing thread for the graph data
     */
    private class GraphManagerThread extends Thread {
        /**
         * Screen refresh Rate in milliseconds
         */
        private final static long sScreenRefreshRate = 17;
        /**
         * Indicate whether the surface has been created & is ready to draw
         */
        private boolean mRun = false;

        public GraphManagerThread() {
            setFocusable(true);
        }

        @Override
        public void run() {
            while (mRun) {
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
                long sleepTime = sScreenRefreshRate - (endTime - startTime);
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
        public void setRun(boolean run) {
            mRun = run;
        }

    }

    /**
     * An interface to update the signals on the graph from an external object without exposing
     * graph manager
     */
    public class GraphSignalInputInterface {
        public InputListener addInput(int id, int sizeOfBuffer, AxisParameters axisParameters,
                                      int color) {
            return mSignalManager.addSignalBuffer(id, sizeOfBuffer, axisParameters, color);
        }

        @SuppressWarnings("SameParameterValue")
        public void removeInput(int id) {
            mSignalManager.removedSignalBuffer(id);
        }

        public ZoomDisplay getGraphXZoomDisplay() {
            return getXZoomDisplay();
        }

        public ZoomDisplay getGraphYZoomDisplay() {
            return getYZoomDisplay();
        }

        public GraphParameters getGraphParameters() {
            return mGraphParameters;
        }
    }
}
