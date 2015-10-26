package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by nick on 25/10/15.
 */
public class Background extends DrawableObject {

    /**
     * the doDraw function will take this color and paint the canvas
     **/
    private int mBackgroundColor = Color.BLACK;

    public Background(DrawableArea drawableArea) {
        super(drawableArea);
    }

    @Override
    public void doDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(mBackgroundColor);
        canvas.drawRect(mDrawableArea.getRect() , paint);
    }

    /**
     * Use this to change the color at any point, changes will be seen after doDraw is called
     **/
    public void setBackgroundColor(int color){
        mBackgroundColor = color;
    }
}
