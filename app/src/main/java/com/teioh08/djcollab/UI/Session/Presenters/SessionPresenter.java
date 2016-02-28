package com.teioh08.djcollab.UI.Session.Presenters;


import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;


public interface SessionPresenter {

    void init(String token);

    String getCurrentQuery();

    void search(String searchQuery);

    void loadMoreResults();

    void selectTrack(Track item);

    void onResume();

    void onPause();

    void onDestroy();

    void resetData();

    void addSearchData(List<Track> items);

    void addPlaylistData(List<Track> items);

    void onQuerySubmit(String query);

    void setupDrawerLayoutListener(Toolbar mToolBar, DrawerLayout mDrawerLayout);

    void onPostCreate();

    void onConfigurationChanged(Configuration newConfig);

    boolean onOptionsSelected(MenuItem item);

    void removeTrack(int pos);

}
