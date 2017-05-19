package com.nfx.android.graph.androidgraph;

import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;

import com.nfx.android.graph.androidgraph.AxisScale.AxisParameters;
import com.nfx.android.graph.graphbufferinput.InputListener;

/**
 * NFX Development
 * Created by nick on 15/01/17.
 */
public interface SignalManagerInterface {
    void addSignal(int id, SignalBuffer signalBuffer, @ColorInt int color);

    InputListener addSignal(int id, int sizeOfBuffer, AxisParameters xAxisParameters,
                            @ColorInt int color);

    boolean hasSignal(int id);

    void removeSignal(int id);

    HorizontalLabelPointer enableTriggerLevelPointer(int signalId, @ColorInt int color);

    @Nullable
    HorizontalLabelPointer getTriggerLevelPointer(int signalId);

    LabelPointer enableXAxisZeroIntersect(@ColorInt int colour);

    void disableXAxisZeroIntersect();


    void enableYAxisIntercept(int signalId);

    void disableYAxisIntercept(int signalId);

    @Nullable
    SignalBuffer signalWithinCatchmentArea(float positionY);

    SignalBufferInterface getSignalBufferInterface(int signalId);

    MarkerManagerInterface getMarkerManagerInterface();
}
