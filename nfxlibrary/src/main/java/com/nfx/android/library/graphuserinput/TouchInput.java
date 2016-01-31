package com.nfx.android.library.graphuserinput;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.nfx.android.library.graphbufferinput.Input;

/**
 * NFX Development
 * Created by nick on 23/11/15.
 * <p/>
 * Implements touch listeners which are needed to scale and move signals on a graph. the Graphview
 * is passed in to listen for touch events and the graph input is passed in to provide information
 * when a touch event occurs
 */
public class TouchInput implements View.OnTouchListener, SurfaceHolder.Callback {
    /**
     * Context
     */
    private final Context mContext;
    /**
     * Handles all scaling gestures
     */
    private final ScaleGestureDetector mScaleGestureDetector;
    /**
     * Handles single tap, double tap, moves and flings
     */
    private final GestureDetectorCompat mGestureDetector;
    /**
     * The listener that wants to be informed of the touch input
     */
    private final TouchListener mTouchListener;

    /**
     * Pass in the view in which to listen for touch inputs from and the input that wants to be
     * informed of the touch inputs
     *
     * @param view  listen to the touch inputs from view
     * @param input pass the touch information onto input
     */
    public TouchInput(SurfaceView view, Input input) {
        view.setOnTouchListener(this);
        mContext = view.getContext();
        mTouchListener = input;

        SurfaceHolder holder = view.getHolder();
        holder.addCallback(this);

        // Sets up interactions
        /*
      The scale listener, used for handling multi-finger scale gestures.
     */
        ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener = new
                ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
                        return mTouchListener.onScaleBegin(scaleGestureDetector);
                    }

                    @Override
                    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                        return mTouchListener.onScale(scaleGestureDetector);
                    }
                };
        mScaleGestureDetector = new ScaleGestureDetector(view.getContext(), mScaleGestureListener);
        GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector
                .SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return mTouchListener.onDown(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return mTouchListener.onSingleTap(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return mTouchListener.onDoubleTap(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float
                    distanceY) {
                return mTouchListener.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float
                    velocityY) {
                return mTouchListener.onFling(e1, e2, velocityX, velocityY);
            }
        };
        mGestureDetector = new GestureDetectorCompat(view.getContext(), mGestureListener);

    }

    /**
     * Called from view to update touch information
     *
     * @param v     the calling view
     * @param event the event that triggered the call
     * @return if the event was handled
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean retVal = mScaleGestureDetector.onTouchEvent(event);
        retVal = mGestureDetector.onTouchEvent(event) || retVal;
        return retVal || v.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context
                .WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        mTouchListener.surfaceChanged(metrics);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    /**
     * Implement this listener within com.nfx.android.library.graphbufferinput.Input
     */
    @SuppressWarnings({"SameReturnValue", "UnusedParameters"})
    public interface TouchListener {
        void surfaceChanged(DisplayMetrics displayMetrics);

        boolean onDown(MotionEvent e);

        boolean onSingleTap(MotionEvent e);

        boolean onDoubleTap(MotionEvent e);

        boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

        boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);

        boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector);

        boolean onScale(ScaleGestureDetector scaleGestureDetector);
    }

}
