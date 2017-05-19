package com.nfx.android.graph.graphbufferinput;

import android.os.Build;

import com.nfx.android.graph.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * NFX Development
 * Created by nick on 23/09/16.
 */
@SuppressWarnings("unused")
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.LOLLIPOP, constants = BuildConfig.class)
public class InputTest {
    private final TestInput testInput = new TestInput();
    private final TestListener testListener = new TestListener();

    @Before
    public void setUp() {
        testInput.addInputListener(testListener);
        testInput.initialise();
        testInput.start();
    }

    @Test
    public void testSetBlockSizeListener() {
        assertThat("Startup buffers are not equal", testInput.getLength(),
                equalTo(testListener.getLength()));
        testInput.setLength(100);
        assertThat("Buffers are not equal after setLength", testInput.getLength(),
                equalTo(testListener.getLength()));
    }

    @Test
    public void testBufferListener() {
        testInput.generateRandomBufferInput();
        assertThat("Buffers are not equal. Listener not working", testInput.getBuffer(),
                equalTo(testListener.getBuffer()));
    }

    @Test
    public void testListenerRemove() {
        testInput.removeInputListener(testListener);
        testInput.generateRandomBufferInput();

        assertThat("Buffers are not equal. Listener not working", testInput.getBuffer(),
                not(equalTo(testListener.getBuffer())));
    }

}