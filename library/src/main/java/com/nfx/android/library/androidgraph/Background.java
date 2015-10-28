package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * NFX Development
 * Created by nick on 25/10/15.
 *
 * Fills the given drawableArea with a color. The default is Black
 */
public class Background extends DrawableObject {

    /**
     * the doDraw function will take this color and paint the canvas
     **/
    private int mBackgroundColor = Color.BLACK;

    @Override
    public void doDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(mBackgroundColor);
        canvas.drawRect(getDrawableArea().getRect(), paint);
    }

    /**
     * Use this to change the color at any point, changes will be seen after doDraw is called
     **/
    public void setBackgroundColor(int color){
        mBackgroundColor = color;
    }
}
