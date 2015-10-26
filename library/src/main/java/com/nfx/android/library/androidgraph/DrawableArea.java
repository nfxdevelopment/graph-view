package com.nfx.android.nfxlibrary.androidgraph;

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

    public DrawableArea(int xOffset, int yOffset, int width, int height) {
        mXOffset = xOffset;
        mYOffset = yOffset;
        mHeight = height;
        mWidth = width;
    }

    public int getXOffset() {
        return mXOffset;
    }

    public int getYOffset() {
        return mYOffset;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getWidth() {
        return mWidth;
    }
}
