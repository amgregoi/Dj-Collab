package com.teioh08.djcollab.UI.Session.Views;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;
import com.teioh08.djcollab.DJCRest.RestClient;
import com.teioh08.djcollab.Party;
import com.teioh08.djcollab.UI.Main.MainActivity;
import com.teioh08.djcollab.UI.Session.Views.Maps.SessionActivityMap;
import com.teioh08.djcollab.Utils.CredentialsHandler;
import com.teioh08.djcollab.R;
import com.teioh08.djcollab.Utils.PlayListScrollListener;
import com.teioh08.djcollab.Utils.ResultListScrollListener;
import com.teioh08.djcollab.UI.Session.Adapters.SessionSongListAdapter;
import com.teioh08.djcollab.UI.Session.Presenters.SessionPresenter;
import com.teioh08.djcollab.UI.Session.Presenters.SessionPresenterImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SessionActivity extends AppCompatActivity implements SessionActivityMap, PlayerNotificationCallback {
    public final static String TAG = SessionActivity.class.getSimpleName();
    private SessionPresenter mSessionPresenter;


    @Bind(R.id.queuedSongs) RecyclerView mSongList;
    @Bind(R.id.searchView) SearchView mSearchView;
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.drawer_layout_list) RecyclerView mPlayList;
    @Bind(R.id.toolbar) Toolbar mToolBar;
    @Bind(R.id.activityTitle) TextView mActivityTitle;

    private Player mMediaPlayer;
    private boolean mIsPlaying;
    private String mCurrentTrack;
    private Party mParty;

    WebSocket ws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        ButterKnife.bind(this);

        String token = CredentialsHandler.getToken(getContext().getApplicationContext());

        if (token == null) {
            Intent main = new Intent(getContext(), MainActivity.class);
            startActivity(main);
        }

        boolean isHost = getIntent().getExtras().getBoolean("isHost");
        mParty = getIntent().getExtras().getParcelable("party");

        mSessionPresenter = new SessionPresenterImpl(this);
        mSessionPresenter.init(CredentialsHandler.getToken(getApplicationContext()), mParty, isHost);
        mSessionPresenter.setupDrawerLayoutListener(mToolBar, mDrawerLayout);

        if (isHost) {
            try {
                ws = new WebSocketFactory().createSocket("ws://djcollab.com/api/v1/host/" + mParty.getId());
                ws.addListener(new WebSocketAdapter() {
                    @Override
                    public void onTextMessage(WebSocket websocket, String message) throws Exception {
                        if (message.contains("id:")) {
                            Log.e(TAG, "socket message : " + message);
                            String hostid = message.substring(3);
                            mParty.setHostId(Integer.parseInt(hostid));
                            RestClient.get().registerHostToParty(mParty.getHostId(), mParty.getId(), new Callback<Void>() {
                                @Override
                                public void success(Void aVoid, retrofit.client.Response response) {
                                    //succesfully registered host
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    //failed to register host
                                }
                            });
                        } else if (message.contains("add:")) {
                            String uri = message.substring(4);
                            play(uri); //queues object
                        }
                        Log.e(TAG, message);
                    }
                });

                Subscription SocketConnection = Observable.create(subscriber -> {
                    try {
                        ws.connect();
                    } catch (WebSocketException e) {
                        e.printStackTrace();
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // Check if result comes from the correct activity
        Config playerConfig = new Config(this, token, "d5a5ea60d29c4c75adde4bf2efadd8e4");
        mMediaPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
            @Override
            public void onInitialized(Player player) {
                mMediaPlayer = player;
                mMediaPlayer.addPlayerNotificationCallback(SessionActivity.this);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });
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
        mSessionPresenter.onDestroy();
        mMediaPlayer.pause();
        mMediaPlayer.shutdown();
        try {
            Spotify.awaitDestroyPlayer(mMediaPlayer, 5000L, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
        }
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
    public void setupSearchview() {
        mSearchView.setOnQueryTextListener(this);

        mSearchView.setOnQueryTextFocusChangeListener((view, queryTextFocused) -> {
            if (!queryTextFocused) {
                mActivityTitle.setVisibility(View.VISIBLE);
                mSearchView.setIconified(true);
                mSearchView.setQuery("", true);
            } else {
                mActivityTitle.setVisibility(View.GONE);
            }
        });
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
    public void closeDrawer() {
        mDrawerLayout.closeDrawers();
    }


    @Override
    public void play(String url) {
        if (mMediaPlayer != null) {
            if (!url.equals(mCurrentTrack))
                mMediaPlayer.queue(url);
        }
    }

    @Override
    public void playlist(List<String> url) {
        if (mMediaPlayer != null) {
            for (String s : url) {
                mMediaPlayer.queue(s);
            }
        }
    }

    @Override
    public void pause() {
        Log.d(TAG, "Pause");
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.shutdown();
            mMediaPlayer = null;
        }
        mIsPlaying = false;
        mCurrentTrack = null;
    }

    @Override
    public void resume() {
        Log.d(TAG, "Resume");
        if (mMediaPlayer != null) {
            mMediaPlayer.resume();
        }
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mIsPlaying;
    }

    @Override
    public boolean isPlayerInitialized() {
        return mMediaPlayer.isInitialized();
    }

    @Override
    @Nullable
    public String getCurrentTrack() {
        return mCurrentTrack;
    }

    boolean isFirst = true;

    @Override
    public void onPlaybackEvent(EventType type, PlayerState state) {
        if (type == EventType.TRACK_START) {
            mIsPlaying = true;
        } else if (type == EventType.TRACK_END) {
            mIsPlaying = false;
        } else if (type == EventType.TRACK_CHANGED) {
            mCurrentTrack = state.trackUri;
            if (!isFirst) {
                mSessionPresenter.removeTrack(0);
            }
            isFirst = false;

        }
    }

    @Override
    public void onPlaybackError(ErrorType type, String s) {
        Log.e(TAG, type + " : " + s);
    }
}
