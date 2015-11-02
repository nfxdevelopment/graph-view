package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Collection;

/**
 * NFX Development
 * Created by nick on 31/10/15.
 */
public class SignalManager {
    private Collection<Signal> mSignalDrawers = new ArrayList<>();

    private SignalBuffers mSignalBuffers;

    public void setSignalBuffers(SignalBuffers signalBuffers) {
        mSignalBuffers = signalBuffers;
        for (SignalBuffer signalBuffer : mSignalBuffers.getSignalBuffer().values()) {
            mSignalDrawers.add(new Signal(signalBuffer));
        }
    }

    /**
     * Call when the surface view changes it's dimensions the objects have to called in the correct
     * order to ensure they take up the correct space
     *
     * @param drawableArea the available area to draw
     */
    public void surfaceChanged(DrawableArea drawableArea) {
        for (Signal signal : mSignalDrawers) {
            signal.surfaceChanged(drawableArea);
        }
    }

    /**
     * Call with the canvas to draw on
     *
     * @param canvas canvas to draw the objects onto
     */
    public void doDraw(Canvas canvas) {
        if (mSignalBuffers != null) {
            for (Signal signal : mSignalDrawers) {
                signal.doDraw(canvas);
            }
        }
    }
}
