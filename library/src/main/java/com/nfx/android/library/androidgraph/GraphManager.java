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
    private Collection<Signal> mSignals = new ArrayList<>();
    /**
     * These object will be passed into drawable objects that need to be resized based on zoom level
     */
    private ZoomDisplay mZoomDisplayX;
    private ZoomDisplay mZoomDisplayY;
    /**
     * The thread that updates the surface
     **/
    private GraphManagerThread mGraphManagerThread;

    public GraphManager(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        mGraphManagerThread = new GraphManagerThread(context, holder);
    }

    public GraphManager(Context context) {
        super(context);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        mGraphManagerThread = new GraphManagerThread(context, holder);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mZoomDisplayX = new ZoomDisplay(0, 1f);
        mZoomDisplayY = new ZoomDisplay(0, 1f);
        mBackgroundManager = new BackgroundManager(mZoomDisplayX, mZoomDisplayY);

        mGraphManagerThread.setRun(true);
        mGraphManagerThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mBackgroundManager.surfaceChanged(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        mGraphManagerThread.setRun(false);
        while (retry) {
            try {
                mGraphManagerThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void doDraw(Canvas canvas) {
        mBackgroundManager.doDraw(canvas);
    }

    public BackgroundManager getBackgroundManager() {
        return mBackgroundManager;
    }

    class GraphManagerThread extends Thread {

        /**
         * Handle to the surface manager object we interact with
         */
        private final SurfaceHolder mSurfaceHolder;
        /**
         * Indicate whether the surface has been created & is ready to draw
         */
        private boolean mRun = false;

        public GraphManagerThread(Context context, SurfaceHolder surfaceHolder) {
            mContext = context;
            mSurfaceHolder = surfaceHolder;

            setFocusable(true);
        }

        @Override
        public void run() {
            while (mRun) {
                Canvas canvas = null;
                try {
                    canvas = mSurfaceHolder.lockCanvas(null);
                    if (canvas != null) {
                        synchronized (mSurfaceHolder) {
                            doDraw(canvas);
                        }
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (canvas != null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
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
}
