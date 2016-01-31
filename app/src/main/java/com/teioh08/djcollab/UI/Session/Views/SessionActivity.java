package com.teioh08.djcollab.UI.Session.Views;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.SearchView;

import com.teioh08.djcollab.Utils.CredentialsHandler;
import com.teioh08.djcollab.R;
import com.teioh08.djcollab.Utils.PlayListScrollListener;
import com.teioh08.djcollab.Utils.ResultListScrollListener;
import com.teioh08.djcollab.UI.Session.Adapters.SessionSongListAdapter;
import com.teioh08.djcollab.UI.Session.Presenters.SessionPresenter;
import com.teioh08.djcollab.UI.Session.Presenters.SessionPresenterImpl;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.models.Track;

public class SessionActivity extends AppCompatActivity implements SessionActivityMap{
    public final static String TAG = SessionActivity.class.getSimpleName();
    private SessionPresenter mSessionPresenter;


    @Bind(R.id.queuedSongs) RecyclerView mSongList;
    @Bind(R.id.searchView) SearchView mSearchView;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.drawer_layout_list) RecyclerView mPlayList;
    @Bind(R.id.toolbar) Toolbar mToolBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        ButterKnife.bind(this);
        mSessionPresenter = new SessionPresenterImpl(this);
        mSessionPresenter.init(CredentialsHandler.getToken(getApplicationContext()));
        mSessionPresenter.setupDrawerLayoutListener(mToolBar, mDrawerLayout);
        mSearchView.setOnQueryTextListener(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSessionPresenter.onPostCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mSessionPresenter.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSessionPresenter.onQuerySubmit(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void reset() {
        mSessionPresenter.resetData();
    }

    @Override
    public void addSearchData(List<Track> items) {
        mSessionPresenter.addSearchData(items);
    }

    @Override
    public void addPlaylistData(List<Track> items) {
        mSessionPresenter.addPlaylistData(items);
    }

    @Override
    public void setupSearchRecyclerView(SessionSongListAdapter adapter, LinearLayoutManager manager, ResultListScrollListener listener) {
        mSongList.setLayoutManager(manager);
        mSongList.setAdapter(adapter);
        mSongList.addOnScrollListener(listener);
        mSongList.setHasFixedSize(true);
    }

    @Override
    public void setupPlaylistRecyclerView(SessionSongListAdapter adapter, LinearLayoutManager manager, PlayListScrollListener listener) {
        mPlayList.setLayoutManager(manager);
        mPlayList.setAdapter(adapter);
        mPlayList.addOnScrollListener(listener);
        mPlayList.setHasFixedSize(true);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setDrawerLayoutListener(ActionBarDrawerToggle mDrawerToggle) {
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onDrawerOpen() {
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
    }

    @Override
    public void onDrawerClose() {
        invalidateOptionsMenu();  // creates call to onPrepareOptionsMenu()
    }

    @Override
    public void closeDrawer(){
        mDrawerLayout.closeDrawers();
    }





}
