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
    protected DrawableArea mDrawableArea;

    public DrawableObject(DrawableArea drawableArea) {
        mDrawableArea = drawableArea;
    }

    /**
     * Called to change the drawable area for the object
     **/
    public void surfaceChange(DrawableArea drawableArea) {
        mDrawableArea = drawableArea;
    }

    public abstract void doDraw(Canvas canvas);

}
