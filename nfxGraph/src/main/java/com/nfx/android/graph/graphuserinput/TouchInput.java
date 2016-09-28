package com.nfx.android.graph.graphuserinput;

import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * NFX Development
 * Created by nick on 23/11/15.
 * <p/>
 * Merges the use of ScaleGestureDetector and GestureDetectorCompat so all touch interactions are
 * passed to the listener through medium
 */
public class TouchInput implements View.OnTouchListener, View.OnLayoutChangeListener {
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
     * Pass in the view in which to listen for touch inputs from and the object that wants to be
     * informed of the touch inputs
     *
     * @param view  listen to the touch inputs from view
     * @param input pass the touch information onto input
     */
    public TouchInput(View view, TouchListener input) {
        view.setOnTouchListener(this);
        mTouchListener = input;

        view.addOnLayoutChangeListener(this);

        // Sets up interactions
        // The scale listener, used for handling multi-finger scale gestures
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

    /**
     * Pass on layout change information only when things have changed
     */
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                               int leftWas, int topWas, int rightWas, int bottomWas) {
        int widthWas = rightWas - leftWas; // right exclusive, left inclusive
        int heightWas = bottomWas - topWas; // bottom exclusive, top inclusive
        if(v.getWidth() != widthWas || v.getHeight() != heightWas) {
            mTouchListener.surfaceChanged(v.getWidth(), v.getHeight());
        }
    }

    /**
     * All events will be passed onto this listener
     */
    public interface TouchListener {
        void surfaceChanged(int width, int height);

        boolean onDown(MotionEvent e);

        boolean onSingleTap(MotionEvent e);

        boolean onDoubleTap(MotionEvent e);

        boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

        boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);

        boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector);

        boolean onScale(ScaleGestureDetector scaleGestureDetector);
    }

}
