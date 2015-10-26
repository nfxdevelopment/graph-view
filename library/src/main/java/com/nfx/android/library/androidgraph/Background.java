package com.nfx.android.nfxlibrary.androidgraph;

import android.graphics.Canvas;
import android.graphics.Color;

/**
 * Created by nick on 25/10/15.
 */
public class Background extends DrawableObject {

    public Background(DrawableArea drawableArea) {
        super(drawableArea);
    }

    @Override
    public void doDraw(Canvas canvas) {
        canvas.drawColor(Color.RED);
    }
}
