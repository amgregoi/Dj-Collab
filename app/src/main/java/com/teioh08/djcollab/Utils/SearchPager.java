package com.teioh08.djcollab.Utils;


import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.SavedTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.client.Response;

public class SearchPager {

    private final SpotifyService mSpotifyApi;
    private int mCurrentOffset;
    private int mPageSize;
    private String mCurrentQuery;

    public interface CompleteListener {
        void onComplete(List<Track> items);
        void onError(Throwable error);
    }

    public SearchPager(SpotifyService spotifyApi) {
        mSpotifyApi = spotifyApi;
    }

    // Search Query
    public void getFirstPageSearch(String query, int pageSize, CompleteListener listener) {
        mCurrentOffset = 0;
        mPageSize = pageSize;
        mCurrentQuery = query;
        getDataSearch(query, 0, pageSize, listener);
    }

    public void getFirstPageSongList(int pageSize, CompleteListener listener) {
        mCurrentOffset = 0;
        mPageSize = pageSize;
        getDataSongList(0, pageSize, listener);
    }

    public void getNextPageSearch(CompleteListener listener) {
        mCurrentOffset += mPageSize;
        getDataSearch(mCurrentQuery, mCurrentOffset, mPageSize, listener);
    }

    private void getDataSearch(String query, int offset, final int limit, final CompleteListener listener) {
        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.OFFSET, offset);
        options.put(SpotifyService.LIMIT, limit);

        mSpotifyApi.searchTracks(query, options, new SpotifyCallback<TracksPager>() {
            @Override
            public void success(TracksPager tracksPager, Response response) {
                listener.onComplete(tracksPager.tracks.items);
            }

            @Override
            public void failure(SpotifyError error) {
                listener.onError(error);
            }
        });
    }


    private void getDataSongList(int offset, final int limit, final CompleteListener listener) {
        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.OFFSET, offset);
        options.put(SpotifyService.LIMIT, limit);

        mSpotifyApi.getMySavedTracks(new SpotifyCallback<Pager<SavedTrack>>() {
            @Override
            public void success(Pager<SavedTrack> pager, Response response) {
                List<Track> newList = new ArrayList<>();
                for(SavedTrack s : pager.items) {
                    newList.add(s.track);
                }
                listener.onComplete(newList);
            }

            @Override
            public void failure(SpotifyError error) {
                Log.e("RAWR", "nope");
                listener.onError(error);
            }
        });
    }
}