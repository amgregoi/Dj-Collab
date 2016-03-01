package com.teioh08.djcollab.UI.Session.Views.Maps;

import android.content.Context;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.SearchView;

import com.teioh08.djcollab.Utils.Player.PlayerInt;
import com.teioh08.djcollab.Widgets.PlayListScrollListener;
import com.teioh08.djcollab.Widgets.ResultListScrollListener;
import com.teioh08.djcollab.UI.Session.Adapters.SongListAdapter;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;


public interface SessionActivityMap{
    void setupPlaylistRecyclerView(SongListAdapter adapter, LinearLayoutManager manager, PlayListScrollListener listener);

    Context getContext();

    void setDrawerLayoutListener(ActionBarDrawerToggle mDrawerToggle);

    void onDrawerOpen();

    void onDrawerClose();

    void closeDrawer();

    void addSongQueue(Track track);
}
