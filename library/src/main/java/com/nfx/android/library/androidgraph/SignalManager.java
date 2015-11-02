package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;

/**
 * NFX Development
 * Created by nick on 31/10/15.
 */
public class SignalManager {
    private Signal mSignalDrawer;

    private SignalBuffers mSignalBuffers;

    SignalManager() {
        mSignalDrawer = new Signal();
    }

    public void setSignalBuffers(SignalBuffers signalBuffers) {
        mSignalBuffers = signalBuffers;
    }

    /**
     * Call when the surface view changes it's dimensions the objects have to called in the correct
     * order to ensure they take up the correct space
     *
     * @param drawableArea the available area to draw
     */
    public void surfaceChanged(DrawableArea drawableArea) {
        mSignalDrawer.surfaceChanged(drawableArea);
    }

    /**
     * Call with the canvas to draw on
     *
     * @param canvas canvas to draw the objects onto
     */
    public void doDraw(Canvas canvas) {
        if (mSignalBuffers != null) {
            for (SignalBuffer signalBuffer : mSignalBuffers.getSignalBuffer().values()) {
                mSignalDrawer.doDraw(canvas, signalBuffer, mSignalBuffers.getSignalScale());
            }
        }
    }
}
