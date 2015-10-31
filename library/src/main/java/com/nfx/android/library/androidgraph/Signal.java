package com.nfx.android.library.androidgraph;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * NFX Development
 * Created by nick on 25/10/15.
 */
public class Signal extends DrawableObject {

    private Paint mSignalPaint;

    private int mColor = Color.YELLOW;
    private float mStrokeWidth = 2f;

    Signal() {
        mSignalPaint = new Paint();
        mSignalPaint.setColor(mColor);
        mSignalPaint.setStrokeWidth(mStrokeWidth);
        mSignalPaint.setAntiAlias(true);
    }

    @Override
    public void doDraw(Canvas canvas) {
        canvas.drawLine(getDrawableArea().getLeft(), getDrawableArea().getHeight() / 2,
                getDrawableArea().getRight(), getDrawableArea().getHeight() / 2, mSignalPaint);
    }

    public void doDraw(Canvas canvas, SignalBuffer signalBuffer,
                       SignalBuffers.SignalScale signalScale, float xScale) {
        if (signalScale == SignalBuffers.SignalScale.linear) {
            doDrawLinear(canvas, signalBuffer, xScale);
        } else {
            doDrawLogarithmic(canvas, signalBuffer, xScale);
        }
    }

    private void doDrawLinear(Canvas canvas, SignalBuffer signalBuffer, float xScale) {
        float[] buffer = new float[signalBuffer.getSizeOfBuffer()];
        System.arraycopy(signalBuffer.getBuffer(), 0, buffer, 0, signalBuffer.getSizeOfBuffer());

        float spacing = (float) getDrawableArea().getWidth() / (float) (buffer.length - 1);

        for (int i = 0; i < signalBuffer.getSizeOfBuffer() - 1; i++) {
            float startPosY = (float) getDrawableArea().getTop() +
                    ((float) getDrawableArea().getHeight() * buffer[i]);
            float startPosX = (float) getDrawableArea().getLeft() + (spacing * (float) i);
            float endPosY = (float) getDrawableArea().getTop() +
                    ((float) getDrawableArea().getHeight() * buffer[i + 1]);
            float endPosX = (float) getDrawableArea().getLeft() + (spacing * (float) (i + 1));

            canvas.drawLine(startPosX, startPosY, endPosX, endPosY, mSignalPaint);
        }
    }

    private void doDrawLogarithmic(Canvas canvas, SignalBuffer signalBuffer, float xScale) {
        float[] buffer = new float[signalBuffer.getSizeOfBuffer()];
        System.arraycopy(signalBuffer.getBuffer(), 0, buffer, 0, signalBuffer.getSizeOfBuffer());

        float screenWidth = (float) getDrawableArea().getWidth();

        float spacing = screenWidth / (float) (buffer.length - 1);
        double maxLogValue = Math.log(screenWidth);

        for (int i = 0; i < signalBuffer.getSizeOfBuffer() - 1; i++) {
            float startPosY = (float) getDrawableArea().getTop() +
                    ((float) getDrawableArea().getHeight() * buffer[i]);

            float startPosXLinear = (spacing * (float) i);
            // This i because the first value will always be 0, as we do not want to lMath.log 0
            if (startPosXLinear == 0) startPosXLinear = 1;
            float startPosX = getDrawableArea().getLeft() +
                    ((float) Math.log(startPosXLinear) / (float) maxLogValue) * screenWidth;

            float endPosY = (float) getDrawableArea().getTop() +
                    ((float) getDrawableArea().getHeight() * buffer[i + 1]);

            float endPosXLinear = spacing * (float) (i + 1);
            float endPosX = getDrawableArea().getLeft() +
                    ((float) Math.log(endPosXLinear) / (float) maxLogValue) * screenWidth;

            canvas.drawLine(startPosX, startPosY, endPosX, endPosY, mSignalPaint);
        }
    }

    @Override
    public void calculateRemainingDrawableArea(DrawableArea currentDrawableArea) {

    }
}
