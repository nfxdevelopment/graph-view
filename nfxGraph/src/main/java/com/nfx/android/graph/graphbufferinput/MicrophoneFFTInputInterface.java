package com.nfx.android.graph.graphbufferinput;

import com.nfx.android.graph.graphbufferinput.windowing.Window;

/**
 * NFX Development
 * Created by nick on 23/01/17.
 */
public interface MicrophoneFFTInputInterface extends InputInterface {
    void setInputFftListener(InputFftListener inputFftListener);

    int getNumberOfHistoryBuffers();

    void setNumberOfHistoryBuffers(int numberOfHistoryBuffers);

    Window getWindow();

    void setWindow(Window window);

    InputFftListener getInputFftListener();
}
