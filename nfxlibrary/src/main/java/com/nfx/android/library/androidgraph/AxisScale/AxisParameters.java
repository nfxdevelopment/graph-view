package com.nfx.android.library.androidgraph.AxisScale;

import com.nfx.android.library.androidgraph.Scale;

/**
 * NFX Development
 * Created by nick on 12/08/16.
 */
public class AxisParameters {
    /**
     * X axis is log or lin
     */
    protected Scale mAxisScale = Scale.linear;
    private float mMinimumValue = 0;
    private float mMaximumValue = 1;

    public AxisParameters(float minimumValue, float maximumValue, Scale axisScale) {
        this.mMinimumValue = minimumValue;
        this.mMaximumValue = maximumValue;
        this.mAxisScale = axisScale;
    }

    /**
     * Find the scaled value that graph Position points to. This is Scale independant
     *
     * @param graphPosition graph position to calculate
     * @return scaled value of the x axis
     */
    public float graphPositionToScaledAxis(float graphPosition) {
        if(mAxisScale == Scale.logarithmic) {
            double minimumValue = mMinimumValue > 0 ? mMinimumValue : 1;
            double maximumValue = Math.log(mMaximumValue / minimumValue) / Math.log(2);
            graphPosition *= maximumValue;

            double frequency = minimumValue * Math.pow(2, graphPosition);

            if(frequency == 1f) {
                return 0f;
            } else {
                return (float) frequency;
            }
        } else {
            return mMinimumValue + (getAxisSpan() * graphPosition);
        }
    }

    /**
     * Calculate the position on graph from a given scaled value. This is Scale independent
     *
     * @param scaleAxisValue real world value to calculate graph position
     * @return graph position the scaled value is portraying
     */
    public float scaledAxisToGraphPosition(float scaleAxisValue) {
        if(mAxisScale == Scale.logarithmic) {
            double minimumValue = mMinimumValue > 0 ? mMinimumValue : 1;
            double maximumValue = Math.log(mMaximumValue / minimumValue) / Math.log(2);
            double result = Math.log(scaleAxisValue / minimumValue) / Math.log(2);

            result /= maximumValue;

            return (float) result;
        } else {
            return (scaleAxisValue - mMinimumValue) / getAxisSpan();
        }
    }


    public float getMinimumValue() {
        return mMinimumValue;
    }

    public void setMinimumValue(float mMinimumValue) {
        this.mMinimumValue = mMinimumValue;
    }

    public float getMaximumValue() {
        return mMaximumValue;
    }

    public void setMaximumValue(float mMaximumValue) {
        this.mMaximumValue = mMaximumValue;
    }

    public Scale getAxisScale() {
        return mAxisScale;
    }

    public void setAxisScale(Scale mAxisScale) {
        this.mAxisScale = mAxisScale;
    }

    public float getAxisSpan() {
        return mMaximumValue - mMinimumValue;
    }
}
