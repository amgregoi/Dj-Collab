package com.teioh08.djcollab.UI.Session.Presenters;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.AsyncTask;
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

import com.teioh08.djcollab.DJCRest.RestClient;
import com.teioh08.djcollab.Party;
import com.teioh08.djcollab.Player.PlayerInt;
import com.teioh08.djcollab.R;
import com.teioh08.djcollab.Services.PreviewService;
import com.teioh08.djcollab.Utils.PlayListScrollListener;
import com.teioh08.djcollab.Utils.PlaylistPager;
import com.teioh08.djcollab.Utils.ResultListScrollListener;
import com.teioh08.djcollab.Utils.SearchPager;
import com.teioh08.djcollab.UI.Session.Views.Maps.SessionActivityMap;
import com.teioh08.djcollab.UI.Session.Adapters.SessionSongListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SessionPresenterImpl implements SessionPresenter {

    private static final String TAG = SessionPresenterImpl.class.getSimpleName();
    public static final int PAGE_SIZE = 20;
    private SessionActivityMap mSessionActivityMap;

    private String mCurrentQuery;
    private PlayerInt mPlayerInt;
    private ActionBarDrawerToggle mDrawerToggle;

    private SearchPager mSearchPager;
    private SearchPager.CompleteListener mSearchCompleteListener;
    private ResultListScrollListener mScrollListener;
    private SessionSongListAdapter mSongListAdapter, mPlayListAdapter;

    private PlaylistPager mPlaylistPager;
    private PlaylistPager.PlaylistCompleteListener mPlaySearchCompleteListener;
    private PlayListScrollListener mPlaylistScrollListener;

    private boolean mIsPartyHost;
    private Party mParty;

    private SpotifyService mService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayerInt = ((PreviewService.PlayerBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlayerInt = null;
        }
    };

    public SessionPresenterImpl(SessionActivityMap map) {
        mSessionActivityMap = map;
    }

    @Override
    public void init(String accessToken, Party p, boolean host) {
        mParty = p;
        mIsPartyHost = host;
        logMessage("Api Client created");
        SpotifyApi spotifyApi = new SpotifyApi();

        if (accessToken != null) spotifyApi.setAccessToken(accessToken);
        else logError("No valid access token");

        mService = spotifyApi.getService();
        mSearchPager = new SearchPager(mService);
        mPlaylistPager = new PlaylistPager(mService);

        mSessionActivityMap.getContext().bindService(PreviewService.getIntent(mSessionActivityMap.getContext()), mServiceConnection, Activity.BIND_AUTO_CREATE);


        setupSearchSongView();
        setupPlaylistView();

        for (String s : mParty.getSongList()) {
            mService.getTrack(s, new SpotifyCallback<Track>() {
                @Override
                public void failure(SpotifyError error) {
                    //failed to get track
                }

                @Override
                public void success(Track track, Response response) {
                    mPlayListAdapter.addSingleData(track);
                    //succesfully got track
                }
            });
        }

        search("Hello, I love you - slight return");
//        getPlayList("garth");

        if(!mIsPartyHost){
            Timer timer = new Timer();
            TimerTask task = new TimerTask(){

                @Override
                public void run() {
                    new MyTask().execute();
                }

            };
            long whenToStart = 20*1000L; // 20 seconds
            long howOften = 20*1000L; // 20 seconds
            timer.scheduleAtFixedRate(task, whenToStart, howOften);
        }

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
//            mSearchPager.getFirstPageSongList(PAGE_SIZE, mSearchCompleteListener);
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
        mPlaylistPager.getFirstPageSearch(searchQuery, PAGE_SIZE, mPlaySearchCompleteListener, new ArrayList<String>());
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
        mSessionActivityMap.getContext().stopService(PreviewService.getIntent(mSessionActivityMap.getContext()));
    }

    @Override
    public void onPause() {
        mSessionActivityMap.getContext().startService(PreviewService.getIntent(mSessionActivityMap.getContext()));
    }

    @Override
    public void loadMoreResults() {
        Log.d(TAG, "Load more...");
        mSearchPager.getNextPageSearch(mSearchCompleteListener);
    }

    //itemClick
    @Override
    public void selectTrack(Track item) {
        String previewUrl = item.preview_url;
        String trackUri = item.uri;
        String id = item.id;

        if (mIsPartyHost) {
            mPlaylistPager.addSong(id, mPlaySearchCompleteListener);
            mSessionActivityMap.play(trackUri);

//            else if (mSessionActivityMap.isPlaying()) mSessionActivityMap.pause();
//            else mSessionActivityMap.resume();

        } else {
            if (previewUrl == null) {
                logMessage("Track doesn't have a preview");
                return;
            }

            if (mPlayerInt == null) return;

            String currentTrackUrl = mPlayerInt.getCurrentTrack();

            RestClient.get().addTrackToParty(mParty.getId(), item.id, new retrofit.Callback<Void>() {
                @Override
                public void success(Void aVoid, Response response) {
                    refreshPlaylist();
//                    mPlayListAdapter.addSingleData(item);
                    //succesfully add song to host list
                }

                @Override
                public void failure(RetrofitError error) {
                    refreshPlaylist();
//                    logError(error.toString());
                    //fail to add song ot  host list
                }
            });

            if (currentTrackUrl == null || !currentTrackUrl.equals(previewUrl))
                mPlayerInt.play(previewUrl);
            else if (mPlayerInt.isPlaying()) mPlayerInt.pause();
            else mPlayerInt.resume();
        }
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
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mSessionActivityMap.onDrawerClose();
            }

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
//        Toast.makeText(mSessionActivityMap.getContext(), msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }

    private void setupSearchSongView() {
        mSessionActivityMap.setupSearchview();
        mSongListAdapter = new SessionSongListAdapter(mSessionActivityMap.getContext(), (itemView, item) -> selectTrack(item), false);

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

    public void temp(){}

    private void setupPlaylistView() {
        mPlayListAdapter = new SessionSongListAdapter(mSessionActivityMap.getContext(), (itemView, item) -> SessionPresenterImpl.this.temp(), true);

        mPlaySearchCompleteListener = new PlaylistPager.PlaylistCompleteListener() {
            @Override
            public void onComplete(Track item) {
                addTrackParty(item);
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, error.getMessage());
            }
        };

        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(mSessionActivityMap.getContext());
        mPlaylistScrollListener = new PlayListScrollListener(mLayoutManager2, mPlaylistPager, mPlaySearchCompleteListener);
        mSessionActivityMap.setupPlaylistRecyclerView(mPlayListAdapter, mLayoutManager2, mPlaylistScrollListener);
    }







    @Override
    public void removeTrack(int pos) {
        if(mPlayListAdapter.getItemCount() > pos) {
            RestClient.get().removeSongPartyList(mParty.getId(), mPlayListAdapter.getTrackAt(pos).id, new Callback<Void>() {
                @Override
                public void success(Void aVoid, Response response) {
                    mPlayListAdapter.removeSingleData(pos);
                }

                @Override
                public void failure(RetrofitError error) {
                    //failed to remove song
                }
            });
        }
    }





    @Override
    public void refreshPlaylist(){
        RestClient.get().getParty(mParty.getId(), new Callback<Party>() {
            @Override
            public void success(Party party, Response response) {
                mParty = party;
                mPlayListAdapter.clearData();
                getTracksFromUri();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void getTracksFromUri(){
        List<Track> newTracks = new ArrayList<>();
        for (String s : mParty.getSongList()) {
            mService.getTrack(s, new SpotifyCallback<Track>() {
                @Override
                public void failure(SpotifyError error) {
                    //failed to get track
                }

                @Override
                public void success(Track track, Response response) {
                    mPlayListAdapter.addSingleData(track);
                    //succesfully got track
                }
            });
        }
    }

    private void addTrackParty(Track item){
        RestClient.get().addTrackToParty(mParty.getId(), item.id, new retrofit.Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {
                mPlayListAdapter.addSingleData(item);
                mPlayerInt.play(item.uri);  //todo remove and add when socket tells you to
                //succesfully add song to host list
            }

            @Override
            public void failure(RetrofitError error) {
                String x = error.getResponse().toString();
//                logError(error.toString());
                //fail to add song ot  host list
            }
        });
    }


    class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // get data from web service
            // insert data in database
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            // refresh UI
            refreshPlaylist();
        }
    }



}

