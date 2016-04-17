package com.teioh08.djcollab.UI.Session.Views.Maps;

import android.content.Context;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;

import com.teioh08.djcollab.Widgets.PlayListScrollListener;
import com.teioh08.djcollab.UI.Session.Adapters.SongListAdapter;

import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.models.Track;


public interface SessionActivityMap {
    void setupPlaylistRecyclerView(SongListAdapter adapter, LinearLayoutManager manager, PlayListScrollListener listener);

    Context getContext();

    void setDrawerLayoutListener(ActionBarDrawerToggle mDrawerToggle);

    void onDrawerOpen();

    void onDrawerClose();

    void closeDrawer();

    void addSongQueue(Track track);

    void openPlaylistFragment(String playlist, String userid, String playlistName);

    void setToolbartitle(String title);

    void setupDrawerLayout(List<String> mDrawerItems, Map<String, List<String>> mSourceCollections);


}
