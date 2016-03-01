package com.teioh08.djcollab.Widgets;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.teioh08.djcollab.Utils.PlaylistPager;

public class PlayListScrollListener extends RecyclerView.OnScrollListener {

    private static final String TAG = ResultListScrollListener.class.getSimpleName();
    private static final int SCROLL_BUFFER = 3;

    private final LinearLayoutManager mLayoutManager;
    private int mCurrentItemCount = 0;
    private boolean mAwaitingItems = true;
    private PlaylistPager mPlaylistPager;
    private PlaylistPager.PlaylistCompleteListener mPlayListener;

    public PlayListScrollListener(LinearLayoutManager layoutManager, PlaylistPager search, PlaylistPager.PlaylistCompleteListener listener) {
        mLayoutManager = layoutManager;
        mPlaylistPager = search;
        mPlayListener = listener;
    }

    public void reset() {
        mCurrentItemCount = 0;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int itemCount = mLayoutManager.getItemCount();
        int itemPosition = mLayoutManager.findLastVisibleItemPosition();

        if (mAwaitingItems && itemCount > mCurrentItemCount) {
            mCurrentItemCount = itemCount;
            mAwaitingItems = false;
        }

        Log.d(TAG, String.format("loading %s, item count: %s/%s, itemPosition %s", mAwaitingItems, mCurrentItemCount, itemCount, itemPosition));

        if (!mAwaitingItems && itemPosition + 1 >= itemCount - SCROLL_BUFFER) {
            mAwaitingItems = true;
            mPlaylistPager.getNextPageSearch(mPlayListener);
        }
    }
}

