package com.nfx.android.graphviewsample;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.nfx.android.graph.androidgraph.AxisScale.AxisParameters;
import com.nfx.android.graph.androidgraph.GraphManager;
import com.nfx.android.graph.androidgraph.Scale;
import com.nfx.android.graph.graphbufferinput.InputListener;

/**
 * NFX Development
 * Created by nick on 19/05/17.
 */
public class GraphViewSample extends AppCompatActivity{

    private final TestInput testInput = new TestInput();
    private GraphManager graphManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_layout);

        graphManager = (GraphManager) findViewById(R.id.graph_manager);

        // create a graph view listener
        InputListener inputListener = graphManager.getSignalManagerInterface().addSignal(
                0,testInput.getBufferSize(), new AxisParameters(0,1, Scale.linear) ,
                ContextCompat.getColor(this,R.color.colorPrimary));

        // link the signal stream to the listener
        testInput.addInputListener(inputListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // start the graph drawer
        graphManager.getGraphViewInterface().start();

        // setup the test signal
        testInput.initialise();
        testInput.start();
        testInput.generateRandomBufferInput();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // stop drawing
        testInput.stop();
        graphManager.getGraphViewInterface().stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        testInput.destroy();
    }
}
