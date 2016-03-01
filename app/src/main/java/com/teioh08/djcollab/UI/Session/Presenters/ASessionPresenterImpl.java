package com.teioh08.djcollab.UI.Session.Presenters;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;
import com.teioh08.djcollab.UI.Session.Views.ASessionActivity;
import com.teioh08.djcollab.Utils.CredentialsHandler;
import com.teioh08.djcollab.Webapi.DJApi;
import com.teioh08.djcollab.Models.Party;
import com.teioh08.djcollab.Utils.Player.PlayerInt;
import com.teioh08.djcollab.R;
import com.teioh08.djcollab.Utils.Player.PreviewService;
import com.teioh08.djcollab.UI.Session.Views.Maps.SessionActivityMap;
import com.teioh08.djcollab.UI.Session.Adapters.SongListAdapter;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ASessionPresenterImpl implements ASessionPresenter, PlayerNotificationCallback {
    private static final String TAG = ASessionPresenterImpl.class.getSimpleName();
    private SessionActivityMap mSessionActivityMap;

    private ActionBarDrawerToggle mDrawerToggle;
    private SongListAdapter mPlayListAdapter;
    private SpotifyService mSpotifyService;
    private Party mParty;
    private PlayerInt mPlayerInt;

    //Host Variables
    private WebSocket ws;
    private Player mMediaPlayer;
    private boolean mIsPlaying, isFirst;
    private String mCurrentTrack;

    //Service Connection
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

    public ASessionPresenterImpl(SessionActivityMap map) {
        mSessionActivityMap = map;
    }

    @Override
    public void init(Bundle bundle) {
        mParty = bundle.getParcelable(ASessionActivity.PARTY_ARGUMENT_KEY);
        boolean mIsPartyHost = bundle.getBoolean(ASessionActivity.ISHOST_ARGUMENT_KEY);
        String accessToken = CredentialsHandler.getToken(mSessionActivityMap.getContext().getApplicationContext());

        //Setup Spotify api/service
        SpotifyApi spotifyApi = new SpotifyApi();
        if (accessToken != null) spotifyApi.setAccessToken(accessToken);
        else logError("No valid access token");
        mSpotifyService = spotifyApi.getService();
        mSessionActivityMap.getContext().bindService(PreviewService.getIntent(mSessionActivityMap.getContext()), mServiceConnection, Activity.BIND_AUTO_CREATE);

        if(mIsPartyHost){
            setupSocketConnection();
        }else{
            Timer timer = new Timer();
            TimerTask task = new TimerTask(){

                @Override
                public void run() {
                    new MyTask().execute();
                }

            };
            long whenToStart = 20*1000L; // 20 seconds
            long howOften = 5*1000L; // 20 seconds
            timer.scheduleAtFixedRate(task, whenToStart, howOften);
        }
        setupPlaylistView();

        // Check if result comes from the correct activity
        Config playerConfig = new Config(mSessionActivityMap.getContext(), accessToken, "d5a5ea60d29c4c75adde4bf2efadd8e4");
        mMediaPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
            @Override
            public void onInitialized(Player player) {
                mMediaPlayer = player;
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("AMainActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });
    }

    @Override
    public void onSavedState() {

    }

    @Override
    public void onRestoreState() {

    }

    @Override
    public void onDestroy() {
        mSessionActivityMap.getContext().unbindService(mServiceConnection);
        mMediaPlayer.pause();
        try {
            Spotify.awaitDestroyPlayer(mMediaPlayer, 5000L, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
        }
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
//        Toast.makeText(mSessionActivityMap.getContext(), "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
    }

    private void logMessage(String msg) {
//        Toast.makeText(mSessionActivityMap.getContext(), msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }

    public void temp(){}

    private void setupPlaylistView() {
        mPlayListAdapter = new SongListAdapter(mSessionActivityMap.getContext(), (itemView, item) -> ASessionPresenterImpl.this.temp(), true);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(mSessionActivityMap.getContext());
        mSessionActivityMap.setupPlaylistRecyclerView(mPlayListAdapter, mLayoutManager2, null);

        //add tracks already in party list
        for (String s : mParty.getSongList()) {
            mSpotifyService.getTrack(s, new SpotifyCallback<Track>() {
                @Override
                public void failure(SpotifyError error) {
                    //failed to retrieve track
                }

                @Override
                public void success(Track track, Response response) {
                    mPlayListAdapter.addSingleData(track);
                    //succesfully retrieved track
                }
            });
        }
    }

    @Override
    public void removeTrack(int pos) {
        if(mPlayListAdapter.getItemCount() > pos) {
            DJApi.get().removeTrackFromParty(mParty.getId(), mPlayListAdapter.getTrackAt(pos).id, new Callback<Void>() {
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
        if(mSessionActivityMap.getContext() != null) {
            DJApi.get().getParty(mParty.getId(), new Callback<Party>() {
                @Override
                public void success(Party party, Response response) {
                    mParty = party;
                    getTracksFromUri();
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

    private void getTracksFromUri() {
        String query = "";
        List<String> currentTracks = mPlayListAdapter.getTrackIds();
        for (String id : mParty.getSongList()) {
            if (!currentTracks.contains(id)) {
                query += id;
            }
        }

        mSpotifyService.getTracks(query, new SpotifyCallback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                mPlayListAdapter.addData(tracks.tracks);
            }

            @Override
            public void failure(SpotifyError error) {
                //failed to get stuff
            }
        });
    }

    private void addTrackParty(Track item){
        DJApi.get().addTrackToParty(mParty.getId(), item.id, new retrofit.Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {
                mPlayListAdapter.addSingleData(item);
                mPlayerInt.queueTrack(item.uri);
                //succesfully add song to host list
            }

            @Override
            public void failure(RetrofitError error) {
                //fail to add song ot  host list
            }
        });
    }

    private void setupSocketConnection(){
        try {
            ws = new WebSocketFactory().createSocket("ws://djcollab.com/api/v1/host/" + mParty.getId());
            ws.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String message) throws Exception {
                    if (message.contains("id:")) {
                        Log.e(TAG, "socket message : " + message);
                        String hostid = message.substring(3);
                        mParty.setHostId(Integer.parseInt(hostid));

                        DJApi.get().registerHostToParty(mParty.getHostId(), mParty.getId(), new Callback<Void>() {
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
                        queueTrack(uri); //queues object
                        refreshPlaylist();
                    }
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

    //refresh task
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





    /*
    *
    *
    *
    *
    *               media player
    *
    *
    *
     */

    @Override
    public void queueTrack(String url) {
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
    @Nullable
    public String getCurrentTrack() {
        return mCurrentTrack;
    }

    @Override
    public void onPlaybackEvent(EventType type, PlayerState state) {
        if (type == EventType.TRACK_START) {
            mIsPlaying = true;
        } else if (type == EventType.TRACK_END) {
            mIsPlaying = false;
        } else if (type == EventType.TRACK_CHANGED) {
            mCurrentTrack = state.trackUri;
            if (!isFirst) {
                removeTrack(0);
            }
            isFirst = false;
        }
    }

    @Override
    public void onPlaybackError(ErrorType type, String s) {

    }
}

