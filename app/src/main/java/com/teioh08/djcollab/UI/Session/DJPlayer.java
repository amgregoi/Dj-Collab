package com.teioh08.djcollab.UI.Session;

import android.support.annotation.Nullable;
import android.util.Log;

import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;


public class DJPlayer implements PlayerNotificationCallback, ConnectionStateCallback {
    public final static String TAG = DJPlayer.class.getSimpleName();

    List<Track> mQueueTracks = new ArrayList<>();
    Player mMediaPlayer;
    private boolean mIsPlaying;
    private String mCurrentTrack = "";
    PlayerListener mPlayerListener;

    public interface PlayerListener{
        void removeTrack(int pos);
    }

    public DJPlayer(Player player, PlayerListener listener) {
        mMediaPlayer = player;
        mMediaPlayer.addPlayerNotificationCallback(this);
        mMediaPlayer.addConnectionStateCallback(this);
        mMediaPlayer.clearQueue();
        mPlayerListener = listener;
    }

    public DJPlayer(PlayerListener listener){
        mPlayerListener = listener;
    }

    public void removeTrack(int pos) {
        mPlayerListener.removeTrack(pos);
        if(mQueueTracks.size() > pos)
            mQueueTracks.remove(pos);
    }

    public void shutdown() {
        if(mMediaPlayer != null) {
            mMediaPlayer.pause();
//            mMediaPlayer.logout();      //necessary?
//            mMediaPlayer.shutdown();    //necessary?
            Spotify.destroyPlayer(mMediaPlayer);
        }
    }

    public void queueTrack(Track track) {
        if (mMediaPlayer != null) {
            if (mQueueTracks.size() == 0) mMediaPlayer.play(track.uri);
            mQueueTracks.add(track);
        }
    }

    public void playlist(List<String> url) {
        if (mMediaPlayer != null) {
            for (String s : url) {
                mMediaPlayer.queue(s);
            }
        }
    }

    public void pause() {
        Log.d(TAG, "Pause");
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.shutdown();
            mMediaPlayer = null;
        }
        mIsPlaying = false;
        mCurrentTrack = "";
    }

    public void resume() {
        Log.d(TAG, "Resume");
        if (mMediaPlayer != null) {
            mMediaPlayer.resume();
        }
    }

    public boolean isPlaying() {
        return mMediaPlayer != null && mIsPlaying;
    }

    @Override
    public void onLoggedIn() {
        Log.d(TAG, "DJPlayer logged in");
    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {

    }

    @Override
    public void onConnectionMessage(String s) {
        Log.e(TAG, s);
    }

    @Nullable
    public String getCurrentTrack() {
        return mCurrentTrack;
    }

    @Override
    public void onPlaybackEvent(PlayerNotificationCallback.EventType type, PlayerState state) {
        if (type == PlayerNotificationCallback.EventType.TRACK_START) {
            mIsPlaying = true;
        } else if (type == PlayerNotificationCallback.EventType.TRACK_END) {
            mIsPlaying = false;
        } else if (type == PlayerNotificationCallback.EventType.END_OF_CONTEXT) {
            mCurrentTrack = state.trackUri;
            if (!mCurrentTrack.equals("")) {
                removeTrack(0);
                if (mQueueTracks.size() > 0) {
                    mMediaPlayer.play(mQueueTracks.get(0).uri);
                    mCurrentTrack = mQueueTracks.get(0).uri;
                } else mCurrentTrack = "";
            }
        }

        Log.d(TAG, "EVENT: " + type.name());
    }

    @Override
    public void onPlaybackError(PlayerNotificationCallback.ErrorType type, String s) {
        Log.d(TAG, type.name() + ": " + s);
    }

}
