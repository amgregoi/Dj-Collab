package com.teioh08.djcollab.UI.Session.Presenters;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.teioh08.djcollab.Player.Player;
import com.teioh08.djcollab.R;
import com.teioh08.djcollab.Services.PlayerService;
import com.teioh08.djcollab.Utils.PlayListScrollListener;
import com.teioh08.djcollab.Utils.PlaylistPager;
import com.teioh08.djcollab.Utils.ResultListScrollListener;
import com.teioh08.djcollab.Utils.SearchPager;
import com.teioh08.djcollab.UI.Session.Views.SessionActivityMap;
import com.teioh08.djcollab.UI.Session.Adapters.SessionSongListAdapter;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;

public class SessionPresenterImpl implements SessionPresenter {

    private static final String TAG = SessionPresenterImpl.class.getSimpleName();
    public static final int PAGE_SIZE = 20;
    private SessionActivityMap mSessionActivityMap;

    private String mCurrentQuery;
    private Player mPlayer;
    private ActionBarDrawerToggle mDrawerToggle;

    private SearchPager mSearchPager;
    private SearchPager.CompleteListener mSearchCompleteListener;
    private ResultListScrollListener mScrollListener;
    private SessionSongListAdapter mSongListAdapter, mPlayListAdapter;

    private PlaylistPager mPlaylistPager;
    private PlaylistPager.CompleteListener mPlayCompleteListener;
    private PlayListScrollListener mPlaylistScrollListener;





    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayer = ((PlayerService.PlayerBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlayer = null;
        }
    };

    public SessionPresenterImpl(SessionActivityMap map) {
        mSessionActivityMap = map;
    }

    @Override
    public void init(String accessToken) {
        logMessage("Api Client created");
        SpotifyApi spotifyApi = new SpotifyApi();

        if (accessToken != null) spotifyApi.setAccessToken(accessToken);
        else logError("No valid access token");

        SpotifyService service =  spotifyApi.getService();
        mSearchPager = new SearchPager(service);
        mPlaylistPager = new PlaylistPager(service);

        mSessionActivityMap.getContext().bindService(PlayerService.getIntent(mSessionActivityMap.getContext()), mServiceConnection, Activity.BIND_AUTO_CREATE);



        setupSearchView();
        setupPlaylistView();

        search("Garth");
        getPlayList("garth");
    }

    @Override
    public void search(@Nullable String searchQuery) {
        if (searchQuery != null && !searchQuery.isEmpty() && !searchQuery.equals(mCurrentQuery)) {
            logMessage("query text submit " + searchQuery);
            mCurrentQuery = searchQuery;
            mSessionActivityMap.reset();
            mSearchCompleteListener = new SearchPager.CompleteListener() {
                @Override
                public void onComplete(List<Track> items) {
                    mSessionActivityMap.addSearchData(items);
                }

                @Override
                public void onError(Throwable error) {
                    logError(error.getMessage());
                }
            };
            mSearchPager.getFirstPageSearch(searchQuery, PAGE_SIZE, mSearchCompleteListener);
        }
    }

    //TODO
    //TODO
    //alter mPlaylistPager to get track info from server and update list
    public void getPlayList(@Nullable String searchQuery) {
            mSearchCompleteListener = new SearchPager.CompleteListener() {
                @Override
                public void onComplete(List<Track> items) {
                    mSessionActivityMap.addPlaylistData(items);
                }

                @Override
                public void onError(Throwable error) {
                    logError(error.getMessage());
                }
            };
            mPlaylistPager.getFirstPageSearch(searchQuery, PAGE_SIZE, mPlayCompleteListener);

    }

    @Override
    public void onDestroy() {
        mSessionActivityMap.getContext().unbindService(mServiceConnection);
    }

    @Override
    public String getCurrentQuery() {
        return mCurrentQuery;
    }

    @Override
    public void onResume() {
        mSessionActivityMap.getContext().stopService(PlayerService.getIntent(mSessionActivityMap.getContext()));
    }

    @Override
    public void onPause() {
        mSessionActivityMap.getContext().startService(PlayerService.getIntent(mSessionActivityMap.getContext()));
    }

    @Override
    public void loadMoreResults() {
        Log.d(TAG, "Load more...");
        mSearchPager.getNextPageSearch(mSearchCompleteListener);
    }

    @Override
    public void selectTrack(Track item) {
        String previewUrl = item.preview_url;

        if(previewUrl == null) {
            logMessage("Track doesn't have a preview");
            return;
        }

        if(mPlayer == null) return;
        String currentTrackUrl = mPlayer.getCurrentTrack();

        if (currentTrackUrl == null || !currentTrackUrl.equals(previewUrl)) mPlayer.play(previewUrl);
        else if (mPlayer.isPlaying()) mPlayer.pause();
        else mPlayer.resume();
    }

    @Override
    public void resetData() {
        mScrollListener.reset();
        mSongListAdapter.clearData();
    }

    @Override
    public void addSearchData(List<Track> items) {
        mSongListAdapter.addData(items);
    }

    @Override
    public void addPlaylistData(List<Track> items) {
        mPlayListAdapter.addData(items);
    }


    @Override
    public void onQuerySubmit(String query) {
        search(query);
    }

    @Override
    public void setupDrawerLayoutListener(Toolbar mToolBar, DrawerLayout mDrawerLayout) {
        mDrawerToggle = new ActionBarDrawerToggle(((Activity) mSessionActivityMap.getContext()), mDrawerLayout,
                mToolBar, R.string.app_name, R.string.app_name) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mSessionActivityMap.onDrawerClose();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mSessionActivityMap.onDrawerOpen();
            }
        };
    }

    @Override
    public void onPostCreate() {
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item);
    }

    private void logError(String msg) {
        Toast.makeText(mSessionActivityMap.getContext(), "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
    }

    private void logMessage(String msg) {
        Toast.makeText(mSessionActivityMap.getContext(), msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }

    private void setupSearchView(){
        mSongListAdapter = new SessionSongListAdapter(mSessionActivityMap.getContext(), new SessionSongListAdapter.ItemSelectedListener() {
            @Override
            public void onItemSelected(View itemView, Track item) {
                selectTrack(item);
            }
        });

        mSearchCompleteListener = new SearchPager.CompleteListener() {
            @Override
            public void onComplete(List<Track> items) {
                mSongListAdapter.addData(items);
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, error.getMessage());
            }
        };

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mSessionActivityMap.getContext());
        mScrollListener = new ResultListScrollListener(mLayoutManager, mSearchPager, mSearchCompleteListener);
        mSessionActivityMap.setupSearchRecyclerView(mSongListAdapter, mLayoutManager, mScrollListener);

    }

    private void setupPlaylistView(){
        mPlayListAdapter = new SessionSongListAdapter(mSessionActivityMap.getContext(), new SessionSongListAdapter.ItemSelectedListener() {
            @Override
            public void onItemSelected(View itemView, Track item) {
                selectTrack(item);
            }
        });

        mPlayCompleteListener = new PlaylistPager.CompleteListener() {
            @Override
            public void onComplete(List<Track> items) {
                mPlayListAdapter.addData(items);
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, error.getMessage());
            }
        };

        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(mSessionActivityMap.getContext());
        mPlaylistScrollListener = new PlayListScrollListener(mLayoutManager2, mPlaylistPager, mPlayCompleteListener);
        mSessionActivityMap.setupPlaylistRecyclerView(mPlayListAdapter, mLayoutManager2, mPlaylistScrollListener);

    }
}

