package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;

/**
 * Created by nick on 25/10/15.
 * <p/>
 * An abstract class which can be inherited to implement a widget that can be drawn onto a Canvas
 */
abstract class DrawableObject {
    /**
     * Advises the background in which area it can draw.
     **/
    private final DrawableArea mDrawableArea = new DrawableArea(0, 0, 0, 0);

    /**
     * Override and implement the specific drawing onto the canvas
     * @param canvas a canvas to draw onto
     */
    protected abstract void doDraw(Canvas canvas);

    /**
     * Get the DrawableArea to change the area in which the object draws in
     * @return the drawableArea of the object
     */
    DrawableArea getDrawableArea() {
        return mDrawableArea;
    }

    /**
     * The surface size has changed update the current object to resize drawing
     *
     * @param drawableArea new surface size
     */
    public void surfaceChanged(DrawableArea drawableArea) {
        getDrawableArea().setDrawableArea(drawableArea);
        calculateRemainingDrawableArea(drawableArea);
    }

    /**
     * This will calculate how much area the object has taken and report back what is left
     *
     * @param currentDrawableArea the drawable area canvas to calculate the area taken
     */
    protected abstract void calculateRemainingDrawableArea(DrawableArea currentDrawableArea);

}
