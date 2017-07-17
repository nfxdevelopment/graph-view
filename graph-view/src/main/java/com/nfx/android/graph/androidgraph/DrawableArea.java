package com.nfx.android.graph.androidgraph;

import android.graphics.Rect;

/**
 * Created by nick on 25/10/15.
 * <p>
 * A helper class to advise drawable objects where to draw within the canvas
 */
class DrawableArea {

    private final Rect rect;
    private int xOffset = 0;
    private int yOffset = 0;
    private int height = 0;
    private int width = 0;

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
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.height = height;
        this.width = width;

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
    void setDrawableArea(int xOffset, int yOffset, int width, int height) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.height = height;
        this.width = width;

        rect.set(xOffset, yOffset, xOffset + width, yOffset + height);
    }

    /**
     * Use this function to be able to change the area at runtime
     *
     * @param drawableArea drawable area to copy from
     */
    public void setDrawableArea(DrawableArea drawableArea) {
        xOffset = drawableArea.xOffset;
        yOffset = drawableArea.yOffset;
        height = drawableArea.height;
        width = drawableArea.width;

        rect.set(xOffset, yOffset, xOffset + width, yOffset + height);
    }

    /**
     * Check and modify position to ensure it is within Y limits
     */
    float checkLimitY(float position) {
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
    float checkLimitX(float position) {
        if (position < getLeft()) {
            return getLeft();
        } else if (position > getRight()) {
            return getRight();
        } else {
            return position;
        }
    }

    Rect getRect() {
        return rect;
    }

    public int getLeft() {
        return xOffset;
    }

    public int getTop() {
        return yOffset;
    }

    public int getRight() {
        return xOffset + width;
    }

    public int getBottom() {
        return yOffset + height;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
