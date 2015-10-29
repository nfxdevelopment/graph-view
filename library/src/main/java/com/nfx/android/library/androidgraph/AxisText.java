package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * NFX Development
 * Created by nick on 29/10/15.
 *
 * Base class for drawing axis text onto a canvas
 */
public abstract class AxisText extends DrawableObject {

    /**
     * string used to calculated maximum string width
     */
    private static final String sWidthTextString = "00";
    /**
     * grid lines that text is relating to
     */
    protected GridLines mGridLines;
    /**
     * paint for the text
     */
    protected Paint mTextPaint = new Paint();
    /**
     * context of application
     */
    private Context mContext;
    /**
     * text size before scaling for screen has been applied
     */
    private int mUnscaledTextSize = 16;

    /**
     * Constructor
     *
     * @param context   application context is used for dimension reasons
     * @param gridLines grid lines axis is related to
     */
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

    /**
     * Uses the static member sWidthTextString to workout the maximum size of axis text this can
     * be changed in the future to reflect a true value
     *
     * @return maximum width of text
     */
    public float getMaximumTextWidth() {
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(sWidthTextString, 0, sWidthTextString.length(), bounds);
        return bounds.width();
    }

    /**
     * Returns the size height of axis text, including the stroke width of the text. This is not
     * the case in the sdk implementations
     *
     * @return text height
     */
    public float getRealTextHeight() {
        return (Math.abs(mTextPaint.ascent()) + Math.abs(mTextPaint.descent()));
    }
}
