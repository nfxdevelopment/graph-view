package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Collection;

/**
 * NFX Development
 * Created by nick on 25/10/15.
 * <p/>
 * The GraphManager handles or instructs the drawing of the graph. It will start displaying when
 * the surface is created and will only stop when the surface is destroyed. It has the ability to
 * handle a resize at runtime.
 */
public class GraphManager extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "GraphManager";
    /**
     * An object to draw all of the background information, including gridlines, axis information
     * and a background color
     */
    BackgroundManager mBackgroundManager;
    /**
     * Handle to the application context, used to e.g. fetch Drawables.
     */
    private Context mContext;
    /**
     * Handles the drawing of a unlimited amount of Markers
     **/
    private Collection<Marker> mMarkers = new ArrayList<>();
    /**
     * Handles the drawing of a unlimited amount of Signals
     **/
    private SignalManager mSignalManager;

    /**
     * The thread that updates the surface
     **/
    private GraphManagerThread mGraphManagerThread;
    /**
     * An interface to update the signal data
     */
    private GraphSignalInputInterface mGraphSignalInputInterface = new GraphSignalInputInterface();

    /**
     * Constructor for graph manager
     *
     * @param context current application context
     * @param attrs   attributes
     */
    public GraphManager(Context context, AttributeSet attrs) {
        this(context);
    }

    /**
     * Constructor for graph manager
     *
     * @param context current application context
     */
    public GraphManager(Context context) {
        super(context);
        mContext = context;

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        mGraphManagerThread = new GraphManagerThread(context);
        mBackgroundManager = new BackgroundManager(mContext, 0, 3, -1, 1);
        mSignalManager = new SignalManager(this);
    }

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
     * @param holder the surface holder that has changed
     * @param format the new pixelformat
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
     * @param holder the surface holder that has been destroyed
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stop();
    }

    /**
     * Start the thread to start displaying the graph
     */
    public void start() {

        mGraphManagerThread = new GraphManagerThread(mContext);
        mGraphManagerThread.setRun(true);
        mGraphManagerThread.start();
    }

    public void stop() {
        mSignalManager.removeSignalDrawers();

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

    protected void doDraw(Canvas canvas) {
        mBackgroundManager.doDraw(canvas);
        mSignalManager.doDraw(canvas);
    }

    public BackgroundManager getBackgroundManager() {
        return mBackgroundManager;
    }

    public SignalManager getSignalManager() {
        return mSignalManager;
    }

    class GraphManagerThread extends Thread {
        /**
         * Indicate whether the surface has been created & is ready to draw
         */
        private boolean mRun = false;

        public GraphManagerThread(Context context) {
            mContext = context;

            setFocusable(true);
        }

        @Override
        public void run() {
            while (mRun) {
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

                try {
                    sleep(20, 0);
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
        public void setSignalBuffers(SignalBuffers signal) {
            mSignalManager.setSignalBuffers(signal);
        }
    }
}
