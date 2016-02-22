package com.nfx.android.library.preference;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * NFX Development
 * Created by nick on 22/02/16.
 * <p/>
 * This object will resize the linear layout to the size of the children it encompasses
 * It is needed as preferenceFragment uses a listView and you cannot wrap a list view. Google it
 */
public class PreferenceFragmentHeightWrap extends PreferenceFragment {

    /**
     * We can guarantee that all children will be in the adaptor list by this point
     */
    @Override
    public void onResume() {
        super.onResume();
        setListViewHeightBasedOnItems(getView());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Sets ListView height dynamically based on the height of the items.
     *
     * @return true if the listView is successfully resized, false otherwise
     */
    public boolean setListViewHeightBasedOnItems(View view) {

        ListView listView = (ListView) view.findViewById(android.R.id.list);
        ListAdapter listAdapter = listView.getAdapter();
        if(listAdapter != null) {

            int numberOfItems = listAdapter.getCount();
            // Get total height of all items.
            int totalItemsHeight = 0;
            for(int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + listView.getPaddingBottom()
                    + listView.getPaddingTop();
            view.setLayoutParams(params);

            return true;

        } else {
            return false;
        }

    }
}
