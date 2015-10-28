package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Color;

/**
 * NFX Development
 * Created by nick on 25/10/15.
 */
public class Marker extends DrawableObject {
    @Override
    public void doDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
    }
}
