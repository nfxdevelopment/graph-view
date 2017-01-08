package com.nfx.android.graph.androidgraph.list.binders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nfx.android.graph.R;
import com.nfx.android.graph.androidgraph.list.data.AverageFrequencyData;
import com.nfx.android.utils.RoundingFormat;
import com.yqritc.recyclerviewmultipleviewtypesadapter.DataBindAdapter;
import com.yqritc.recyclerviewmultipleviewtypesadapter.DataBinder;

import java.util.ArrayList;
import java.util.List;

/**
 * NFX Development
 * Created by nick on 7/01/17.
 */
public class AverageFrequencyBinder extends DataBinder<AverageFrequencyBinder.ViewHolder> {
    private List<AverageFrequencyData> dataSet = new ArrayList<>();

    public AverageFrequencyBinder(DataBindAdapter dataBindAdapter) {
        super(dataBindAdapter);
    }

    @Override
    public AverageFrequencyBinder.ViewHolder newViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.average_frequency_list_view, parent, false);
        return new AverageFrequencyBinder.ViewHolder(view);
    }

    @Override
    public void bindViewHolder(ViewHolder holder, int position) {
        AverageFrequencyData averageFrequencyData = dataSet.get(position);

        holder.signalColour.setBackgroundColor(averageFrequencyData.getSignalColor());
        holder.averageFrequency.setText(RoundingFormat.frequencyToString(
                averageFrequencyData.getAverageFrequency()));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void addAll(List<AverageFrequencyData> dataSet) {
        this.dataSet.addAll(dataSet);
        notifyBinderDataSetChanged();
    }

    public void clear() {
        int sizeOfList = dataSet.size();
        dataSet.clear();
        notifyBinderItemRangeRemoved(0, sizeOfList);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View signalColour;
        final TextView averageFrequency;

        ViewHolder(View base) {
            super(base);
            signalColour = base.findViewById(R.id.signal_colour);
            averageFrequency = (TextView) base.findViewById(R.id.average_frequency_value);
        }
    }
}
