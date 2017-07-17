package com.nfx.android.graph.androidgraph;

import android.graphics.Canvas;
import android.graphics.Color;

/**
 * NFX Development
 * Created by nick on 25/10/15.
 * <p>
 * Fills the given drawableArea with a color. The default is Black
 */
class Background extends DrawableObject {

    /**
     * the doDraw function will take this color and paint the canvas
     **/
    private static final int INITIAL_COLOR = Color.BLACK;

    Background() {
        paint.setColor(INITIAL_COLOR);
    }

    @Override
    public void doDraw(Canvas canvas) {
        canvas.drawRect(getDrawableArea().getRect(), paint);
    }

    /**
     * The background is a underlay and is considered a underlay there we do not change the
     * drawable area.
     *
     * @param currentDrawableArea will reflect the new drawable area pass in current drawableArea
     */
    @Override
    public void calculateRemainingDrawableArea(DrawableArea currentDrawableArea) {
    }
}
