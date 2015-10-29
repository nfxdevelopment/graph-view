package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * NFX Development
 * Created by nick on 29/10/15.
 */
public abstract class AxisText extends DrawableObject {

    private static final String sWidthTextString = "00";
    protected GridLines mGridLines;
    protected Paint mTextPaint = new Paint();
    private Context mContext;
    private int mUnscaledTextSize = 16;

    AxisText(Context context, GridLines gridLines) {
        mContext = context;
        mGridLines = gridLines;

        float textScale = mContext.getResources().getDisplayMetrics().density;

        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setTextSize((float) mUnscaledTextSize * textScale);
        mTextPaint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
    }

    public float getMaximumTextWidth() {
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(sWidthTextString, 0, sWidthTextString.length(), bounds);
        return bounds.width();
    }

    public float getRealTextHeight() {
        return (Math.abs(mTextPaint.ascent()) + Math.abs(mTextPaint.descent()));
    }
}
