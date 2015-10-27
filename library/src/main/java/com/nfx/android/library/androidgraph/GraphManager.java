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
    private static final float sGridLineStrokeWidth = 5;
    /**
     * Handle to the application context, used to e.g. fetch Drawables.
     */
    private Context mContext;
    /**
     * An object which draws onto the canvas
     **/
    private Background mBackground;
    /**
     * Handles the drawing of each axis text
     **/
    private XAxis mXAxis;
    private YAxis mYAxis;
    /**
     * Handles the drawing of all grid lines
     */
    private Collection<GridLines> mGridLinesMajor = new ArrayList<>();
    private Collection<GridLines> mGridLinesMinor = new ArrayList<>();
    /**
     * Handles the drawing of a unlimited amount of Markers
     **/
    private Collection<Marker> mMarkers = new ArrayList<>();
    /**
     * Handles the drawing of a unlimited amount of Signals
     **/
    private Collection<Signal> mSignals = new ArrayList<>();
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
        mBackground = new Background(new DrawableArea(0, 0, 0, 0));
        mXAxis = new LinXAxis(new DrawableArea(0, 0, 0, 0));
        mXAxis.setGridStrokeWidth(sGridLineStrokeWidth);

        mYAxis = new LinYAxis(new DrawableArea(0, 0, 0, 0));
        mYAxis.setGridStrokeWidth(sGridLineStrokeWidth);

        mGridLinesMajor.add(new LinYGridLines(new DrawableArea(0, 0, 0, 0)));
        mGridLinesMajor.add(new LinXGridLines(new DrawableArea(0, 0, 0, 0)));
        mGridLinesMinor.add(new LogXGridLines(new DrawableArea(0, 0, 0, 0)));

        mGraphManagerThread.setRun(true);
        mGraphManagerThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mBackground.surfaceChange(new DrawableArea(0, 0, getWidth(), getHeight()));
        for (GridLines gridLines : mGridLinesMajor) {
            gridLines.surfaceChange(new DrawableArea(0, 0, getWidth(), getHeight()));

            if (gridLines.getAxisOrientation() == GridLines.AxisOrientation.xAxis) {
                XGridLines xGridLinesMajor = (XGridLines) gridLines;
                for (GridLines gridLinesMinor : mGridLinesMinor) {
                    if (gridLinesMinor.getAxisOrientation() == GridLines.AxisOrientation.xAxis) {
                        int xOffset = (int) xGridLinesMajor.xIntersect(0);
                        int minorGridLineWidth = (int) xGridLinesMajor.xIntersect(1) - xOffset;
                        gridLinesMinor.surfaceChange(new DrawableArea(xOffset, 0,
                                minorGridLineWidth, getHeight()));
                    }
                }
            }
        }


//        mXAxis.surfaceChange(new DrawableArea(0, height - getPaddingBottom() -
//                (int) sGridLineStrokeWidth, width, (int) sGridLineStrokeWidth));
//
//        mYAxis.surfaceChange(new DrawableArea(0, 0, (int) sGridLineStrokeWidth, height));
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
        mBackground.doDraw(canvas);
//        mXAxis.doDraw(canvas);
//        mYAxis.doDraw(canvas);

        for (GridLines gridLines : mGridLinesMajor) {
            gridLines.doDraw(canvas);
        }
        for (GridLines gridLines : mGridLinesMinor) {
            gridLines.doDraw(canvas);
        }
    }

    public Background getMBackground(){
        return mBackground;
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
