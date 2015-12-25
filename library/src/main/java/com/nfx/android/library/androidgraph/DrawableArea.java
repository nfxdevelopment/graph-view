package com.nfx.android.library.androidgraph;

import android.graphics.Rect;

/**
 * Created by nick on 25/10/15.
 * <p/>
 * A helper class to advise drawable objects where to draw within the canvas
 */
class DrawableArea {

    private final Rect rect;
    private int mXOffset = 0;
    private int mYOffset = 0;
    private int mHeight = 0;
    private int mWidth = 0;

    /**
     * Initial values can be set in the constructor
     *
     * @param xOffset far left side of area
     * @param yOffset top most side of area
     * @param width   width dimension
     * @param height  height dimension
     */
    @SuppressWarnings("SameParameterValue")
    public DrawableArea(int xOffset, int yOffset, int width, int height) {
        mXOffset = xOffset;
        mYOffset = yOffset;
        mHeight = height;
        mWidth = width;

        rect = new Rect(xOffset, yOffset, xOffset + width, yOffset + height);
    }

    /**
     * Use this function to be able to change the area at runtime
     *
     * @param xOffset far left side of area
     * @param yOffset top most side of area
     * @param width   width dimension
     * @param height  height dimension
     */
    public void setDrawableArea(int xOffset, int yOffset, int width, int height) {
        mXOffset = xOffset;
        mYOffset = yOffset;
        mHeight = height;
        mWidth = width;

        rect.set(xOffset, yOffset, xOffset + width, yOffset + height);
    }

    /**
     * Use this function to be able to change the area at runtime
     *
     * @param drawableArea drawable area to copy from
     */
    public void setDrawableArea(DrawableArea drawableArea) {
        mXOffset = drawableArea.mXOffset;
        mYOffset = drawableArea.mYOffset;
        mHeight = drawableArea.mHeight;
        mWidth = drawableArea.mWidth;

        rect.set(mXOffset, mYOffset, mXOffset + mWidth, mYOffset + mHeight);
    }

    /**
     * Check and modify position to ensure it is within Y limits
     */
    public float checkLimitY(float position) {
        if (position < getTop()) {
            return getTop();
        } else if (position > getBottom()) {
            return getBottom();
        } else {
            return position;
        }
    }

    /**
     * Check and modify position to ensure it is within Y limits
     */
    public float checkLimitX(float position) {
        if (position < getLeft()) {
            return getLeft();
        } else if (position > getRight()) {
            return getRight();
        } else {
            return position;
        }
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
