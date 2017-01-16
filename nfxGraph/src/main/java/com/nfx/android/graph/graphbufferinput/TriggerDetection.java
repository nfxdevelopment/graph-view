package com.nfx.android.graph.graphbufferinput;

import android.os.SystemClock;

/**
 * NFX Development
 * Created by nick on 17/12/16.
 */
public class TriggerDetection {
    /**
     * The type of trigger the class is detecting
     */
    private TriggerType triggerType = TriggerType.RISING;
    /**
     * A value between min value and max value
     */
    private float triggerValue = 0.75f;
    /**
     * The time delay between triggers
     */
    private long holdOffDelayInMs = 0;
    private long lastTriggerTiming = 0;

    /**
     * Compare the last value and the current value to determine if trigger is crossed
     *
     * @param lastValue    the value previous to the current value
     * @param currentValue the current value being analysed
     * @return true if the the trigger is crossed
     */
    public boolean hasTriggered(float lastValue, float currentValue) {
        boolean hasTriggered = false;
        if(SystemClock.elapsedRealtime() - lastTriggerTiming >= holdOffDelayInMs) {
            switch(triggerType) {
                case RISING:
                    hasTriggered = hasTriggeredRising(lastValue, currentValue);
                    break;
                case FALLING:
                    hasTriggered = hasTriggeredFalling(lastValue, currentValue);
                    break;
                case BOTH:
                    hasTriggered = hasTriggeredFalling(lastValue, currentValue) ||
                            hasTriggeredRising(lastValue, currentValue);
                    break;
            }
            if(hasTriggered) {
                lastTriggerTiming = SystemClock.elapsedRealtime();
            }
        } else {
            hasTriggered = false;
        }

        return hasTriggered;
    }

    private boolean hasTriggeredRising(float lastValue, float currentValue) {
        return currentValue > triggerValue && lastValue < triggerValue;
    }

    private boolean hasTriggeredFalling(float lastValue, float currentValue) {
        return currentValue < triggerValue && lastValue > triggerValue;
    }

    /**
     * @return the current trigger type of object
     */
    public TriggerType getTriggerType() {
        return triggerType;
    }

    /**
     * Set the trigger type of object
     *
     * @param triggerType trigger type to set
     */
    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    /**
     * @return the current trigger value
     */
    public float getTriggerValue() {
        return triggerValue;
    }

    /**
     * Set the current trigger value
     *
     * @param triggerValue the trigger value to set
     */
    public void setTriggerValue(float triggerValue) {
        this.triggerValue = triggerValue;
    }

    /**
     * @return the current hold off delay
     */
    public long getHoldOffDelayInMs() {
        return holdOffDelayInMs;
    }

    /**
     * Set the hold off delay in millisecond
     *
     * @param holdOffDelayInMs time in milliseconds
     */
    public void setHoldOffDelayInMs(long holdOffDelayInMs) {
        this.holdOffDelayInMs = holdOffDelayInMs;
    }

    public enum TriggerType {
        RISING,
        FALLING,
        BOTH
    }
}
