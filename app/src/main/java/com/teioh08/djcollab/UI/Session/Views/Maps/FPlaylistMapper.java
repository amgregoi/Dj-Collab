package com.teioh08.djcollab.UI.Session.Views.Maps;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.SearchView;

import com.teioh08.djcollab.UI.Session.Adapters.SongListAdapter;
import com.teioh08.djcollab.Widgets.PlayListScrollListener;
import com.teioh08.djcollab.Widgets.PlaylistResultScrollListener;
import com.teioh08.djcollab.Widgets.ResultListScrollListener;

/**
 * Created by amgregoi on 2/29/16.
 */
public interface FPlaylistMapper extends SearchView.OnQueryTextListener{

    void setupSearchRecyclerView(SongListAdapter adapter, LinearLayoutManager manager, PlaylistResultScrollListener listener);

    Context getContext();

    void reset();

    void setupSearchview();

    void setupToolbarTitle(String name);

}
