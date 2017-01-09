package com.nfx.android.graph.androidgraph;

import android.graphics.Canvas;
import android.util.Log;

/**
 * NFX Development
 * Created by nick on 9/01/17.
 */
class HorizontalLabelPointer extends LabelPointer {
    private static final String TAG = HorizontalLabelPointer.class.getName();
    private ZoomDisplay zoomDisplay;

    HorizontalLabelPointer(ZoomDisplay zoomDisplay) {
        this.zoomDisplay = zoomDisplay;
    }

    @Override
    public void doDraw(Canvas canvas) {
        float xPositionOfCircle = getXPositionOfPointer();
        float yPositionOfCircle = getYPositionOfPointer();

        canvas.drawCircle(getDrawableArea().checkLimitX(xPositionOfCircle),
                getDrawableArea().checkLimitY(yPositionOfCircle),
                circleRadius, paint);

        if(showLine) {
            canvas.drawLine(
                    getDrawableArea().getLeft(),
                    getDrawableArea().checkLimitY(yPositionOfCircle),
                    getDrawableArea().getRight(),
                    getDrawableArea().checkLimitY(yPositionOfCircle),
                    paint
            );
        }
    }

    private float getYPositionOfPointer() {
        float intersect = location;

        intersect -= zoomDisplay.getDisplayOffsetPercentage();
        intersect /= zoomDisplay.getZoomLevelPercentage();
        intersect = 1f - intersect;

        intersect *= getDrawableArea().getHeight();
        intersect += getDrawableArea().getTop();

        if(intersect < getDrawableArea().getTop()) {
            return getDrawableArea().getTop();
        } else if(intersect > getDrawableArea().getBottom()) {
            return getDrawableArea().getBottom();
        }

        return intersect;
    }

    private float getXPositionOfPointer() {
        if(getAlignment() == Alignment.start) {
            return getDrawableArea().getLeft() + circleRadius;
        } else if(getAlignment() == Alignment.end) {
            return getDrawableArea().getRight() - circleRadius;
        } else {
            Log.e(TAG, "Alignment not recognized drawing circle at 0");
            return 0;
        }
    }
}
