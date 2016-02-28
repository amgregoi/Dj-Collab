package com.teioh08.djcollab.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.client.Response;

public class PlaylistPager {

    private final SpotifyService mSpotifyApi;
    private int mCurrentOffset;
    private int mPageSize;
    private String mCurrentQuery;
    private List<String> mPlaylist;

    public interface PlaylistCompleteListener {
        void onComplete(Track items);
        void onError(Throwable error);
    }

    public PlaylistPager(SpotifyService spotifyApi) {
        mSpotifyApi = spotifyApi;
    }

    // Search Query
    public void getFirstPageSearch(String query, int pageSize, PlaylistCompleteListener listener, List<String> playlist) {
        mCurrentOffset = 0;
        mPageSize = pageSize;
        mCurrentQuery = query;
        mPlaylist = new ArrayList<>(playlist);

        getData(query, 0, pageSize, listener);
    }

    public void getNextPageSearch(PlaylistCompleteListener listener) {
        mCurrentOffset += mPageSize;

        getData(mCurrentQuery, mCurrentOffset, mPageSize, listener);
    }

    public void addSong(String id, PlaylistCompleteListener listener){
        getData(id, mCurrentOffset, mPageSize, listener);
    }


    private void getData(String query, int offset, final int limit, final PlaylistCompleteListener listener) {
        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.OFFSET, offset);
        options.put(SpotifyService.LIMIT, limit);

        mSpotifyApi.getTrack(query, options, new SpotifyCallback<Track>() {
            @Override
            public void success(Track track, Response response) {
                listener.onComplete(track);

            }

            @Override
            public void failure(SpotifyError error) {
                listener.onError(error);
            }
        });
    }
}