package com.teioh08.djcollab.Utils;


import android.util.Log;

import com.teioh08.djcollab.Webapi.ExtraSpotifyApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.SavedTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserPlaylistPager {

    private final SpotifyService mSpotifyApi;
    private int mCurrentOffset;
    private int mPageSize;
    private String mToken;

    private String userId, partyId;

    public interface CompleteListener {
        void onComplete(List<Track> items);
        void onError(Throwable error);
    }

    public UserPlaylistPager(SpotifyService spotifyApi, String userid, String partyid, String token) {
        mSpotifyApi = spotifyApi;
        userId = userid;
        partyId = partyid;
        mToken = token;

    }

    // Search Query
    public void getFirstPageSearch(int pageSize, CompleteListener listener) {
        mCurrentOffset = 0;
        mPageSize = pageSize;
        getDataSearch(0, pageSize, listener);
    }

    public void getNextPageSearch(CompleteListener listener) {
        mCurrentOffset += mPageSize;
        getDataSearch(mCurrentOffset, mPageSize, listener);
    }

    private void getDataSearch(int offset, final int limit, final CompleteListener listener) {
        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.OFFSET, offset);
        options.put(SpotifyService.LIMIT, limit);

        ExtraSpotifyApi.get().getPlaylistTracks("Bearer "+mToken, userId, partyId, new SpotifyCallback<Pager<PlaylistTrack>>() {
            @Override
            public void success(Pager<PlaylistTrack> pager, Response response) {
                List<Track> newTracks = new ArrayList<>();
                for (PlaylistTrack plt : pager.items) {
                    newTracks.add(plt.track);
                }

                listener.onComplete(newTracks);
            }

            @Override
            public void failure(SpotifyError error) {
                listener.onError(error);
            }
        });
    }
}