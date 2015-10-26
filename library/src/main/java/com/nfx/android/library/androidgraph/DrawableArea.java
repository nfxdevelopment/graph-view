package com.nfx.android.library.androidgraph;

import android.graphics.Rect;

/**
 * Created by nick on 25/10/15.
 * <p/>
 * A helper class to advise drawable objects where to draw within the canvas
 */
public class DrawableArea {

    private int mXOffset = 0;
    private int mYOffset = 0;
    private int mHeight = 0;
    private int mWidth = 0;

    private Rect rect;

    public DrawableArea(int xOffset, int yOffset, int width, int height) {
        mXOffset = xOffset;
        mYOffset = yOffset;
        mHeight = height;
        mWidth = width;

        rect = new Rect(xOffset , yOffset , xOffset + width , yOffset + height );
    }

    public Rect getRect(){ return rect; }

    public int getLeft() { return mXOffset; }

    public int getTop() { return mYOffset; }

    public int getRight() {
        return mXOffset + mWidth;
    }

    public int getBottom() {
        return mYOffset + mHeight;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getWidth() {
        return mWidth;
    }
}
