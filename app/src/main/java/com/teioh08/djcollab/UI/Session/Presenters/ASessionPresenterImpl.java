package com.teioh08.djcollab.UI.Session.Presenters;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
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
import android.view.View;
import android.widget.Toast;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;
import com.teioh08.djcollab.UI.Session.Views.ASessionActivity;
import com.teioh08.djcollab.Utils.CredentialsHandler;
import com.teioh08.djcollab.Utils.SharedPrefsUtil;
import com.teioh08.djcollab.Webapi.DJApi;
import com.teioh08.djcollab.Models.Party;
import com.teioh08.djcollab.Utils.Player.PlayerInt;
import com.teioh08.djcollab.R;
import com.teioh08.djcollab.Utils.Player.PreviewService;
import com.teioh08.djcollab.UI.Session.Views.Maps.SessionActivityMap;
import com.teioh08.djcollab.UI.Session.Adapters.SongListAdapter;
import com.teioh08.djcollab.Webapi.ExtraSpotifyApi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import kaaes.spotify.webapi.android.models.UserPrivate;
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
    private UserPrivate mPrivateUser;

    //Host Variables
    private WebSocket ws;
    private Player mMediaPlayer;
    private boolean mIsPlaying;
    private String mCurrentTrack = "";
    private boolean mIsPartyHost;
    private String mAccessToken;


    private List<PlaylistSimple> baseUserPlaylists;

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
        mIsPartyHost = bundle.getBoolean(ASessionActivity.ISHOST_ARGUMENT_KEY);
        mAccessToken = CredentialsHandler.getToken(mSessionActivityMap.getContext());

        mSessionActivityMap.setToolbartitle(mParty.getName());

        SpotifyApi spotifyApi = new SpotifyApi();

        if (mAccessToken != null) spotifyApi.setAccessToken(mAccessToken);
        else logError("No valid access token");
        mSpotifyService = spotifyApi.getService();
        mSessionActivityMap.getContext().bindService(PreviewService.getIntent(mSessionActivityMap.getContext()), mServiceConnection, Activity.BIND_AUTO_CREATE);


        if (mIsPartyHost) {
            setupSocketConnection();
        } else {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {

                @Override
                public void run() {
                    new RefreshPlayListTask().execute();
                }

            };
            long whenToStart = 20 * 1000L; // 20 seconds
            long howOften = 5 * 1000L; // 20 seconds
            timer.scheduleAtFixedRate(task, whenToStart, howOften);
        }
        setupPlaylistView();
        refreshPlaylist();
        setupDrawerLayouts();
        setupPlayer();

    }

    private void setupPlayer(){
        // Check if result comes from the correct activity
        Config playerConfig = new Config(mSessionActivityMap.getContext(), mAccessToken, "d5a5ea60d29c4c75adde4bf2efadd8e4");
        mMediaPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
            @Override
            public void onInitialized(Player player) {
                mMediaPlayer = player;
                mMediaPlayer.addPlayerNotificationCallback(ASessionPresenterImpl.this);
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
    public void spotifyAuthenticationResult(int requestCode, int resultCode, Intent intent) {
        // Check if result comes from the correct activity
        if (requestCode == CredentialsHandler.REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                case TOKEN:
                    logMessage("Got token: " + response.getAccessToken());
                    CredentialsHandler.setToken(mSessionActivityMap.getContext(), response.getAccessToken(), response.getExpiresIn(), TimeUnit.SECONDS);
                    SpotifyApi spotifyApi = new SpotifyApi();
                    mAccessToken = response.getAccessToken();
                    spotifyApi.setAccessToken(mAccessToken);
                    mSpotifyService = spotifyApi.getService();
                    setupDrawerLayouts();
                    break;
                case ERROR:
                    logError("Auth error: " + response.getError());
                    break;
                default:
                    logError("Auth result: " + response.getType());
            }
        }
    }

    @Override
    public void onDrawerItemChosen(int pos) {
        if (pos == 0) {
            if (mAccessToken == null) { // login
                final AuthenticationRequest request = new AuthenticationRequest.Builder(CredentialsHandler.CLIENT_ID, AuthenticationResponse.Type.TOKEN, CredentialsHandler.REDIRECT_URI)
                        .setScopes(new String[]{"playlist-read", "user-library-read", "playlist-read-private", "user-read-private", "user-library-modify", "user-read-private"})
                        .build();

                AuthenticationClient.openLoginActivity(((ASessionActivity) mSessionActivityMap), CredentialsHandler.REQUEST_CODE, request);
            } else {
                toastMessage("Already logged in!");
            }
        } else if (pos == 1) {
            if (mSessionActivityMap.getContext() != null) {
                DJApi.get().requestPartyHost(mParty.getHostId(), mParty.getId(), new Callback<Void>() {
                    @Override
                    public void success(Void aVoid, Response response) {
//                        mIsPartyHost = true;
//                        setupSocketConnection();
//                        setupPlayer();
                        toastMessage("Not yet implemented");

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "failed to take host");
                        toastMessage("Not yet implemented");
                    }
                });
            }
        }
    }

    @Override
    public void onPlaylistChosen(int pos) {
        mSessionActivityMap.openPlaylistFragment(baseUserPlaylists.get(pos).id, mPrivateUser.id, baseUserPlaylists.get(pos).name);
    }

    @Override
    public void removeTrack(int pos) {
        if (mPlayListAdapter.getItemCount() > pos) {
            mQueueTracks.remove(0);
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
    public void refreshPlaylist() {
        if (mSessionActivityMap.getContext() != null) {
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

    private void logError(String msg) {
//        Toast.makeText(mSessionActivityMap.getContext(), "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
    }

    private void logMessage(String msg) {
//        Toast.makeText(mSessionActivityMap.getContext(), msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }

    private void toastMessage(String msg) {
        Toast.makeText(mSessionActivityMap.getContext(), msg, Toast.LENGTH_SHORT).show();

    }

    private void temp() {
    }

    private void setupPlaylistView() {
        mPlayListAdapter = new SongListAdapter(mSessionActivityMap.getContext(), (itemView, item) -> ASessionPresenterImpl.this.temp(), true);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(mSessionActivityMap.getContext());
        mSessionActivityMap.setupPlaylistRecyclerView(mPlayListAdapter, mLayoutManager2, null);
    }

    private void getTracksFromUri() {
        String query = "";
        for (String id : mParty.getSongList())
            query += id + ",";

        if (query.contains(",")) query = query.substring(0, query.length() - 1);

        mSpotifyService.getTracks(query, new SpotifyCallback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                List<Track> oldList = mPlayListAdapter.getTracks();
                List<Track> currList = tracks.tracks;
                List<Track> removeList = new ArrayList<Track>();

                //removes old song from the top until both lists are synced
                for (Track old : oldList) {
                    if (currList.size() > 0 && !old.equals(currList.get(0))) {
                        removeList.add(old);
                    } else break;
                }
                oldList.removeAll(removeList);

                //adds new songs to end
                for (Track track : currList) {
                    if (!oldList.contains(track)) mPlayListAdapter.addSingleData(track);
                }
            }

            @Override
            public void failure(SpotifyError error) {
                //failed to get stuff
            }
        });
    }

    private void setupSocketConnection() {
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

                    logError(message);
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

    class RefreshPlayListTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            // refresh UI
            refreshPlaylist();
        }
    }


    private void setupDrawerLayouts() {
        if(mPrivateUser == null) {
            mSpotifyService.getMe(new Callback<UserPrivate>() {
                @Override
                public void success(UserPrivate user, Response response) {
                    SharedPrefsUtil.setSpotifyUsername(user.id);
                    mPrivateUser = user;            // used later to determine if user is premium or free
                    buildDrawerItems();
                }

                @Override
                public void failure(RetrofitError error) {
                    buildDrawerItems();
                }
            });
        }else{
            buildDrawerItems();
        }
    }

    private void buildDrawerItems(){
        List<String> mDrawerItems = new ArrayList<>();
        mDrawerItems.add("Login to Spotify");
        mDrawerItems.add("Request Host");
        baseUserPlaylists = new ArrayList<>();

        ExtraSpotifyApi.get().getUserPlaylists("Bearer " + mAccessToken, new SpotifyCallback<Pager<PlaylistSimple>>() {
            @Override
            public void success(Pager<PlaylistSimple> pager, Response response) {
                for (PlaylistSimple pl : pager.items) {
                    baseUserPlaylists.add(pl);
                }

                mDrawerItems.add("Playlists");
                Map<String, List<String>> mSourceCollections = new LinkedHashMap<>();
                for (String item : mDrawerItems) {
                    List<String> mDrawerChildren = new ArrayList<>();
                    if (item.equals("Playlists")) {
                        for (PlaylistSimple p : pager.items)
                            mDrawerChildren.add(p.name);
                    }
                    mSourceCollections.put(item, mDrawerChildren);
                }
                mSessionActivityMap.setupDrawerLayout(mDrawerItems, mSourceCollections);

            }

            @Override
            public void failure(SpotifyError error) {
                System.out.println("test");
                Map<String, List<String>> mSourceCollections = new LinkedHashMap<>();
                mSessionActivityMap.setupDrawerLayout(mDrawerItems, mSourceCollections);
            }

        });
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

    List<Track> mQueueTracks = new ArrayList<>();

    @Override
    public void queueTrack(String url) {
        if (mMediaPlayer != null) {
            mSpotifyService.getTrack(url, new Callback<Track>() {
                @Override
                public void success(Track track, Response response) {
                    if (mQueueTracks.size() == 0) {
                        mMediaPlayer.play(track.uri);
//                        mCurrentTrack = track.uri;
                    }
                    mQueueTracks.add(track);
                }

                @Override
                public void failure(RetrofitError error) {
                    //failed
                }
            });
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
        mCurrentTrack = "";
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
        logError(TAG + " : " + type.name());
        if (type == EventType.TRACK_START) {
            mIsPlaying = true;
        } else if (type == EventType.TRACK_END) {
            mIsPlaying = false;
        } else if (type == EventType.END_OF_CONTEXT) {
            mCurrentTrack = state.trackUri;
            if (!mCurrentTrack.equals("")) {
                removeTrack(0);
                if (mQueueTracks.size() > 0) {
                    mMediaPlayer.play(mQueueTracks.get(0).uri);
                    mCurrentTrack = mQueueTracks.get(0).uri;
                } else mCurrentTrack = "";
            }
//            isFirst = false;
        }
    }

    @Override
    public void onPlaybackError(ErrorType type, String s) {

    }
}

