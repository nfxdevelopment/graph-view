package com.nfx.android.library.androidgraph.markerList;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nfx.android.library.R;

import java.util.List;

/**
 * NFX Development
 * Created by nick on 5/08/16.
 * <p/>
 * An adaptor to display all the marker details in a floating widget
 */
public class MarkerAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<MarkerModel> mList;
    private final Handler mHandler;
    private final Runnable mRefreshListRun;

    public MarkerAdapter(Context context, List<MarkerModel> list) {
        mContext = context;
        mList = list;
        mHandler = new Handler(context.getMainLooper());
        mRefreshListRun = new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int pos) {
        return mList.get(pos);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        MarkerViewHolder viewHolder;
        if(convertView == null) {
            LayoutInflater li = (LayoutInflater)
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.marker_list_view, parent, false);
            viewHolder = new MarkerViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (MarkerViewHolder) v.getTag();
        }
        int maxChars = 6;
        String xValue = String.valueOf((int) mList.get(position).getXValue());
        String yValue = String.valueOf(mList.get(position).getYValue());
        if(yValue.length() > maxChars) {
            yValue = yValue.substring(0, maxChars);
        }

        viewHolder.mXValue.setText(xValue);
        viewHolder.mYValue.setText(yValue);
        viewHolder.mMarkerColour.setBackgroundColor(mList.get(position).getMarkerColor());
        return v;
    }

    void refreshList() {
        mHandler.post(mRefreshListRun);
    }
}

class MarkerViewHolder {
    final TextView mXValue;
    final TextView mYValue;
    final View mMarkerColour;

    MarkerViewHolder(View base) {
        mXValue = (TextView) base.findViewById(R.id.marker_x_value);
        mYValue = (TextView) base.findViewById(R.id.marker_y_value);
        mMarkerColour = base.findViewById(R.id.marker_colour);
    }
}