package com.nfx.android.graph.androidgraph.list.binders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nfx.android.graph.R;
import com.nfx.android.graph.androidgraph.list.data.MarkerData;
import com.yqritc.recyclerviewmultipleviewtypesadapter.DataBindAdapter;
import com.yqritc.recyclerviewmultipleviewtypesadapter.DataBinder;

import java.util.ArrayList;
import java.util.List;

/**
 * NFX Development
 * Created by nick on 7/01/17.
 */
public class MarkerBinder extends DataBinder<MarkerBinder.ViewHolder> {
    private List<MarkerData> dataSet = new ArrayList<>();

    public MarkerBinder(DataBindAdapter dataBindAdapter) {
        super(dataBindAdapter);
    }

    @Override
    public ViewHolder newViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.marker_list_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindViewHolder(ViewHolder holder, int position) {
        MarkerData markerData = dataSet.get(position);

        holder.markerColour.setBackgroundColor(markerData.getMarkerColor());
        int maxChars = 6;

        String xValueString;

        if(markerData.xIsInteger()) {
            xValueString = String.valueOf((int) markerData.getXValue());
        } else {
            xValueString = String.valueOf(markerData.getXValue());
        }
        if(xValueString.length() > maxChars) {
            xValueString = xValueString.substring(0, maxChars);
        }

        holder.xValue.setText(xValueString);

        String yValueString;

        if(markerData.yIsInteger()) {
            yValueString = String.valueOf((int) markerData.getYValue());
        } else {
            yValueString = String.valueOf(markerData.getYValue());
        }
        if(yValueString.length() > maxChars) {
            yValueString = yValueString.substring(0, maxChars);
        }

        holder.yValue.setText(yValueString);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void addAll(List<MarkerData> dataSet) {
        this.dataSet.addAll(dataSet);
        notifyBinderDataSetChanged();
    }

    public void clear() {
        int sizeOfList = dataSet.size();
        dataSet.clear();
        notifyBinderItemRangeRemoved(0, sizeOfList);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView xValue;
        final TextView yValue;
        final View markerColour;

        ViewHolder(View base) {
            super(base);
            xValue = (TextView) base.findViewById(R.id.marker_x_value);
            yValue = (TextView) base.findViewById(R.id.marker_y_value);
            markerColour = base.findViewById(R.id.marker_colour);
        }
    }
}
