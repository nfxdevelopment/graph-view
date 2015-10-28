package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;

/**
 * Created by nick on 25/10/15.
 * <p/>
 * An abstract class which can be inherited to implement a widget that can be drawn onto a Canvas
 */
public abstract class DrawableObject {
    /**
     * Advises the background in which area it can draw.
     **/
    private DrawableArea mDrawableArea = new DrawableArea(0, 0, 0, 0);

    /**
     * Override and implement the specific drawing onto the canvas
     *
     * @param canvas a canvas to draw onto
     */
    public abstract void doDraw(Canvas canvas);

    /**
     * Get the DrawableArea to change the area in which the object draws in
     *
     * @return the drawableArea of the object
     */
    public DrawableArea getDrawableArea() {
        return mDrawableArea;
    }

}
