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
    private Scale mAxisScale = Scale.linear;
    /**
     * Minimum value of the axis
     */
    private float mMinimumValue = 0;
    /**
     * Maximum value of the axis
     */
    private float mMaximumValue = 1;

    /**
     * @param minimumValue initial minimum value of the axis
     * @param maximumValue initial maximum value of the axis
     * @param axisScale    which scale is the axis
     */
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

    /**
     * @return minimum value of the axis
     */
    public float getMinimumValue() {
        return mMinimumValue;
    }

    /**
     * Set th minimum value of the axis
     *
     * @param mMinimumValue minimum value
     */
    public void setMinimumValue(float mMinimumValue) {
        this.mMinimumValue = mMinimumValue;
    }

    /**
     * @return maximum value of the axis
     */
    public float getMaximumValue() {
        return mMaximumValue;
    }

    /**
     * Set th maximum value of the axis
     *
     * @param mMaximumValue minimum value
     */
    public void setMaximumValue(float mMaximumValue) {
        this.mMaximumValue = mMaximumValue;
    }

    /**
     * @return the scale of the axis
     */
    public Scale getAxisScale() {
        return mAxisScale;
    }

    /**
     * Set the scale of the axis
     *
     * @param mAxisScale axis scale
     */
    public void setAxisScale(Scale mAxisScale) {
        this.mAxisScale = mAxisScale;
    }

    /**
     * @return the span between minimum and maximum
     */
    public float getAxisSpan() {
        return mMaximumValue - mMinimumValue;
    }
}
