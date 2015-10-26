package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Color;

/**
 * NFX Development
 * Created by nick on 25/10/15.
 */
public class Axis extends DrawableObject {

    /**
     * The color in which the lines will be drawn
     **/
    protected int mGridColor = Color.GRAY;
    protected float mGridStrokeWidth = 5f;

    public Axis(DrawableArea drawableArea) {
        super(drawableArea);
    }

    @Override
    public void doDraw(Canvas canvas) {

    }

    public void setGridStrokeWidth(float strokeWidth) {
        mGridStrokeWidth = strokeWidth;
    }
}
