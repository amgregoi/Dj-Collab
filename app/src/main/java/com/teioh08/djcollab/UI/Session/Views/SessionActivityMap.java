package com.teioh08.djcollab.UI.Session.Views;

import android.content.Context;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.SearchView;

import com.teioh08.djcollab.Utils.PlayListScrollListener;
import com.teioh08.djcollab.Utils.ResultListScrollListener;
import com.teioh08.djcollab.UI.Session.Adapters.SessionSongListAdapter;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;


public interface SessionActivityMap extends SearchView.OnQueryTextListener {

    void reset();

    void addSearchData(List<Track> items);

    void addPlaylistData(List<Track> items);

    void setupSearchRecyclerView(SessionSongListAdapter adapter, LinearLayoutManager manager, ResultListScrollListener listener);

    void setupPlaylistRecyclerView(SessionSongListAdapter adapter, LinearLayoutManager manager, PlayListScrollListener listener);

    Context getContext();

    void setDrawerLayoutListener(ActionBarDrawerToggle mDrawerToggle);

    void onDrawerOpen();

    void onDrawerClose();

    void closeDrawer();


}
