package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * NFX Development
 * Created by nick on 29/10/15.
 */
public class YAxisText extends AxisText {
    YAxisText(Context context, GridLines gridLines) {
        super(context, gridLines);
    }

    @Override
    public void doDraw(Canvas canvas) {
        for (int i = 0; i < mGridLines.getNumberOfGridLines(); ++i) {
            Rect bounds = new Rect();
            String textString = String.valueOf(i);
            mTextPaint.getTextBounds(textString, 0, textString.length(), bounds);
            int y = (int) mGridLines.intersect(i) + (bounds.height() / 2);

            canvas.drawText(textString, getDrawableArea().getLeft(), y, mTextPaint);
        }
    }
}
