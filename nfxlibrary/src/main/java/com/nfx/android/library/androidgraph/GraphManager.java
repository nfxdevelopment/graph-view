package com.nfx.android.library.androidgraph;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.nfx.android.library.R;
import com.nfx.android.library.androidgraph.markerList.MarkerAdapter;
import com.nfx.android.library.androidgraph.markerList.MarkerModel;

import java.util.ArrayList;
import java.util.List;

/**
 * NFX Development
 * Created by nick on 22/07/16.
 */
public class GraphManager extends RelativeLayout {
    private GraphView mGraphView;
    private ListView mMarkerInformation;
    private List<MarkerModel> mMarkerList;
    private MarkerAdapter mMarkerAdapter;

    public GraphManager(Context context) {
        super(context);
        initialiseWithoutAttributes();
    }

    public GraphManager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialiseWithAttributes(attrs);
    }

    public GraphManager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialiseWithAttributes(attrs);
    }

    private void initialiseWithAttributes(AttributeSet attr) {
        mGraphView = new GraphView(getContext(), attr);
        initialise();
    }

    private void initialiseWithoutAttributes() {
        mGraphView = new GraphView(getContext());
        initialise();
    }

    private void initialise() {
        inflate(getContext(), R.layout.nfx_graph, this);

        if(mGraphView.getParent() != null) {
            ((ViewGroup) mGraphView.getParent()).removeView(mGraphView);
        }
        FrameLayout graphView = (FrameLayout)findViewById(R.id.graph);
        graphView.addView(mGraphView);
        mMarkerInformation = (ListView)findViewById(R.id.marker_information);

        mMarkerList = new ArrayList<>();
        mMarkerAdapter = new MarkerAdapter(getContext(), mMarkerList);

        mMarkerInformation.setAdapter(mMarkerAdapter);

        mGraphView.getBackgroundManager().setBackgroundColour(
                getContext().getResources().getColor(R.color.background));
        mGraphView.getBackgroundManager().setGridLineColour(
                getContext().getResources().getColor(R.color.gridLines));
    }

    public void setMarkers(int signalId, boolean isShown) {
        if(isShown) {
            Resources resources = getContext().getResources();
            addMarker(signalId, resources.getColor(R.color.marker1));
            addMarker(signalId, resources.getColor(R.color.marker2));
        } else {
            removeMarker(signalId);
            mMarkerList.clear();
        }

        mMarkerAdapter.notifyDataSetChanged();
    }

    private void addMarker(int signalId, int colour) {
        MarkerModel markerModel = new MarkerModel(mMarkerAdapter,
                getGraphView().getGraphSignalInputInterface());
        mMarkerList.add(markerModel);
        getGraphView().getSignalManager().addMarker(colour, signalId, markerModel);
    }

    private void removeMarker(int signalId) {
        getGraphView().getSignalManager().removeMarkers(signalId);
    }

    public GraphView getGraphView() {
        return mGraphView;
    }

    public ListView getMarkerInformation() {
        return mMarkerInformation;
    }
}
