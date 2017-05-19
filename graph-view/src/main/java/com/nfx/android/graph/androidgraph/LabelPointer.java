package com.nfx.android.graph.androidgraph;

/**
 * NFX Development
 * Created by nick on 9/01/17.
 */
public abstract class LabelPointer extends DrawableObject {

    /**
     * Pointer circle radius
     */
    final static float circleRadius = 15;
    private final static float MAXIMUM_LOCATION = 1f;
    private final static float MINIMUM_LOCATION = 0f;
    /**
     * Where the point is drawn. This is a value from 0 - 1
     */
    float location = 0.5f;
    boolean showLine = false;
    /**
     * Draw the point at start or end of the line
     */
    private Alignment alignment = Alignment.start;

    @Override
    protected void calculateRemainingDrawableArea(DrawableArea currentDrawableArea) {
        // This object can be drawn over, so do not change the drawable area
    }

    public abstract float getYPositionOfPointer();

    public abstract float getXPositionOfPointer();

    Alignment getAlignment() {
        return alignment;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public float getLocation() {
        return location;
    }

    public void setLocation(float location) {
        if(location > MAXIMUM_LOCATION) {
            location = MAXIMUM_LOCATION;
        } else if(location < MINIMUM_LOCATION) {
            location = MINIMUM_LOCATION;
        }

        this.location = location;
    }

    public boolean isShowLine() {
        return showLine;
    }

    public void setShowLine(boolean showLine) {
        this.showLine = showLine;
    }

    /**
     * Used to decide whether the pointer is drawn atthe start or the end of the line
     */
    enum Alignment {
        start,
        end
    }
}
