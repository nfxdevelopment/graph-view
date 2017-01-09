package com.nfx.android.graph.androidgraph;

import android.graphics.Canvas;
import android.util.Log;

/**
 * NFX Development
 * Created by nick on 9/01/17.
 */
class VerticalLabelPointer extends LabelPointer {
    private static final String TAG = VerticalLabelPointer.class.getName();

    /**
     * GraphView interface
     */
    protected final ZoomDisplay zoomDisplay;

    VerticalLabelPointer(ZoomDisplay zoomDisplay) {
        super();
        this.zoomDisplay = zoomDisplay;
    }

    @Override
    public void doDraw(Canvas canvas) {
        float xPositionOfCircle = getXPositionOfPointer();
        float yPositionOfCircle = getYPositionOfPointer();

        canvas.drawCircle(getDrawableArea().checkLimitX(xPositionOfCircle), yPositionOfCircle,
                circleRadius, paint);

        if(showLine) {
            canvas.drawLine(
                    getDrawableArea().checkLimitX(xPositionOfCircle),
                    getDrawableArea().getTop(),
                    getDrawableArea().checkLimitX(xPositionOfCircle),
                    getDrawableArea().getBottom(),
                    paint
            );
        }
    }

    private float getXPositionOfPointer() {
        float intersect = location;

        intersect -= zoomDisplay.getDisplayOffsetPercentage();
        intersect /= zoomDisplay.getZoomLevelPercentage();

        intersect *= getDrawableArea().getWidth();
        intersect += getDrawableArea().getLeft();

        if(intersect < getDrawableArea().getLeft()) {
            return getDrawableArea().getLeft();
        } else if(intersect > getDrawableArea().getRight()) {
            return getDrawableArea().getRight();
        }

        return intersect;
    }

    private float getYPositionOfPointer() {
        if(getAlignment() == Alignment.start) {
            return getDrawableArea().getTop() + circleRadius;
        } else if(getAlignment() == Alignment.end) {
            return getDrawableArea().getBottom() - circleRadius;
        } else {
            Log.e(TAG, "Alignment not recognized drawing circle at 0");
            return 0;
        }
    }
}
