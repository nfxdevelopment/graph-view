package com.nfx.android.library.graphbufferinput;

import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.nfx.android.library.androidgraph.GraphManager;
import com.nfx.android.library.androidgraph.SignalBuffers;
import com.nfx.android.library.androidgraph.ZoomDisplay;
import com.nfx.android.library.graphuserinput.TouchInput;

/**
 * NFX Development
 * Created by nick on 10/11/15.
 */
public abstract class Input implements TouchInput.TouchListener {

    private static final float sMinimumSpan = 200f;
    /**
     * Signal object to send to the graph
     */
    protected final SignalBuffers mSignalBuffers = new SignalBuffers();
    protected boolean mPaused = false;
    /**
     * The interface in which to send updates to
     */
    protected GraphManager.GraphSignalInputInterface mGraphSignalInputInterface;
    private DisplayMetrics mDisplayMetrics;
    private float lastSpanX;
    private float lastSpanY;

    Input(GraphManager.GraphSignalInputInterface graphSignalInputInterface) {
        mGraphSignalInputInterface = graphSignalInputInterface;
    }

    /**
     * Initialise anything that needs to be setup prior to start
     */
    public abstract void initialise();

    /**
     * Start the listeners
     */
    public abstract void start();

    /**
     * Stop the listeners
     */
    public abstract void stop();

    /**
     * destroy the buffers and listeners getting ready to die
     */
    public abstract void destroy();


    @Override
    public void surfaceChanged(DisplayMetrics displayMetrics) {
        mDisplayMetrics = displayMetrics;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onSingleTap(MotionEvent e) {
        mPaused = !mPaused;
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        scrollHandle(getZoomDisplayX(), distanceX, mDisplayMetrics.widthPixels);
        scrollHandle(getZoomDisplayY(), distanceY, mDisplayMetrics.heightPixels);

        return true;
    }

    private void scrollHandle(ZoomDisplay zoomDisplay, float distanceMoved, float displaySize) {
        float displayOffset = zoomDisplay.getDisplayOffsetPercentage();

        float movementPercentage = (distanceMoved / displaySize) *
                zoomDisplay.getZoomLevelPercentage();

        zoomDisplay.setDisplayOffsetPercentage(displayOffset + movementPercentage);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        lastSpanX = scaleGestureDetector.getCurrentSpanX();
        lastSpanY = scaleGestureDetector.getCurrentSpanY();

        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {

        // Deal with the span changes for x and y. This is only based on the previous span
        float currentSpanX = scaleGestureDetector.getCurrentSpanX();
        if(currentSpanX > sMinimumSpan) {
            float scaleFactorX = lastSpanX / currentSpanX;
            lastSpanX = currentSpanX;
            scaleHandle(getZoomDisplayX(), scaleFactorX);

            // We want to span from the focal point of the users fingers, use display offset to
            // shift
            // the signal over whilst scaling
            float focusX = scaleGestureDetector.getFocusX();
            offsetAccountedScaleHandle(getZoomDisplayX(), scaleFactorX, focusX,
                    mDisplayMetrics.widthPixels);
        } else {
            lastSpanX = currentSpanX;
        }

        float currentSpanY = scaleGestureDetector.getCurrentSpanY();
        if(currentSpanY > sMinimumSpan) {
            float scaleFactorY = lastSpanY / currentSpanY;
            lastSpanY = currentSpanY;
            scaleHandle(getZoomDisplayY(), scaleFactorY);

            float focusY = scaleGestureDetector.getFocusY();

            offsetAccountedScaleHandle(getZoomDisplayY(), scaleFactorY, focusY,
                    mDisplayMetrics.heightPixels);
        } else {
            lastSpanY = currentSpanY;
        }

        return true;
    }

    private void scaleHandle(ZoomDisplay zoomDisplay, float scaleFactor) {
        float displayZoom = zoomDisplay.getZoomLevelPercentage();

        float zoomPercentage = displayZoom * scaleFactor;

        zoomDisplay.setZoomLevelPercentage(zoomPercentage);
    }

    private void offsetAccountedScaleHandle(ZoomDisplay zoomDisplay, float scaleFactor,
                                            float focusPoint, float displaySize) {
        float displayOffset = zoomDisplay.getDisplayOffsetPercentage();
        float displayFarSide = zoomDisplay.getFarSideOffsetPercentage();
        //Calculate the focal point of the users finger based on the whole signal
        float focusPointPercentage = displayOffset + ((displayFarSide - displayOffset) *
                (focusPoint / displaySize));

        float newOffset = displayOffset + ((focusPointPercentage - displayOffset) *
                (1 - scaleFactor));

        zoomDisplay.setDisplayOffsetPercentage(newOffset);
    }

    private ZoomDisplay getZoomDisplayX() {
        return mSignalBuffers.getSignalBuffer().get(0).getXZoomDisplay();
    }

    private ZoomDisplay getZoomDisplayY() {
        return mSignalBuffers.getSignalBuffer().get(0).getYZoomDisplay();
    }
}
