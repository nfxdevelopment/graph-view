package com.nfx.android.graph.androidgraph.AxisScale;

import com.nfx.android.graph.androidgraph.Scale;

/**
 * NFX Development
 * Created by nick on 12/08/16.
 */
public class AxisParameters {
    /**
     * X axis is log or lin
     */
    private Scale axisScale = Scale.linear;
    /**
     * Minimum value of the axis
     */
    private float minimumValue = 0;
    /**
     * Maximum value of the axis
     */
    private float maximumValue = 1;

    /**
     * @param minimumValue initial minimum value of the axis
     * @param maximumValue initial maximum value of the axis
     * @param axisScale    which scale is the axis
     */
    public AxisParameters(float minimumValue, float maximumValue, Scale axisScale) {
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.axisScale = axisScale;
    }

    /**
     * Find the scaled value that graph Position points to. This is Scale independant
     *
     * @param graphPosition graph position to calculate
     * @return scaled value of the x axis
     */
    public float graphPositionToScaledAxis(float graphPosition) {
        if(axisScale == Scale.logarithmic) {
            double minimumValue = this.minimumValue > 0 ? this.minimumValue : 1;
            double maximumValue = Math.log(this.maximumValue / minimumValue) / Math.log(2);
            graphPosition *= maximumValue;

            double frequency = minimumValue * Math.pow(2, graphPosition);

            if(frequency == 1f) {
                return 0f;
            } else {
                return (float) frequency;
            }
        } else {
            return minimumValue + (getAxisSpan() * graphPosition);
        }
    }

    /**
     * Calculate the position on graph from a given scaled value. This is Scale independent
     *
     * @param scaleAxisValue real world value to calculate graph position
     * @return graph position the scaled value is portraying
     */
    public float scaledAxisToGraphPosition(float scaleAxisValue) {
        if(axisScale == Scale.logarithmic) {
            double minimumValue = this.minimumValue > 0 ? this.minimumValue : 1;
            double maximumValue = Math.log(this.maximumValue / minimumValue) / Math.log(2);
            double result = Math.log(scaleAxisValue / minimumValue) / Math.log(2);

            result /= maximumValue;

            return (float) result;
        } else {
            return (scaleAxisValue - minimumValue) / getAxisSpan();
        }
    }

    /**
     * @return minimum value of the axis
     */
    public float getMinimumValue() {
        return minimumValue;
    }

    /**
     * Set th minimum value of the axis
     *
     * @param mMinimumValue minimum value
     */
    public void setMinimumValue(float mMinimumValue) {
        this.minimumValue = mMinimumValue;
    }

    /**
     * @return maximum value of the axis
     */
    public float getMaximumValue() {
        return maximumValue;
    }

    /**
     * Set th maximum value of the axis
     *
     * @param mMaximumValue minimum value
     */
    public void setMaximumValue(float mMaximumValue) {
        this.maximumValue = mMaximumValue;
    }

    /**
     * @return the scale of the axis
     */
    public Scale getAxisScale() {
        return axisScale;
    }

    /**
     * Set the scale of the axis
     *
     * @param mAxisScale axis scale
     */
    public void setAxisScale(Scale mAxisScale) {
        this.axisScale = mAxisScale;
    }

    /**
     * @return the span between minimum and maximum
     */
    public float getAxisSpan() {
        return maximumValue - minimumValue;
    }
}
