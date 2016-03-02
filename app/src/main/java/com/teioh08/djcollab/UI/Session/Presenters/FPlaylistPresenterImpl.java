package com.teioh08.djcollab.UI.Session.Presenters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.teioh08.djcollab.Models.Party;
import com.teioh08.djcollab.UI.Session.Adapters.SongListAdapter;
import com.teioh08.djcollab.UI.Session.Views.Maps.FPlaylistMapper;
import com.teioh08.djcollab.UI.Session.Views.ASessionActivity;
import com.teioh08.djcollab.Utils.CredentialsHandler;
import com.teioh08.djcollab.Utils.SearchPager;
import com.teioh08.djcollab.Utils.UserPlaylistPager;
import com.teioh08.djcollab.Webapi.DJApi;
import com.teioh08.djcollab.Widgets.PlaylistResultScrollListener;
import com.teioh08.djcollab.Widgets.ResultListScrollListener;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class FPlaylistPresenterImpl implements FPlaylistPresenter {
    final public static String TAG = FSearchTrackPresenterImpl.class.getSimpleName();
    public static final int PAGE_SIZE = 20;

    private FPlaylistMapper mFPlaylistMapper;

    public FPlaylistPresenterImpl(FPlaylistMapper map) {
        mFPlaylistMapper = map;
    }

    private UserPlaylistPager mUserPlaylistPager;
    private UserPlaylistPager.CompleteListener mPlaylistCompleteListener;
    private PlaylistResultScrollListener mScrollListener;
    private SongListAdapter mSongListAdapter;

    private boolean mIsPartyHost;
    private Party mParty;

    private SpotifyService mSpotifyService;

    @Override
    public void init(Bundle bundle) {
        mFPlaylistMapper.setupSearchview();

        mIsPartyHost = bundle.getBoolean(ASessionActivity.ISHOST_ARGUMENT_KEY);
        mParty = bundle.getParcelable(ASessionActivity.PARTY_ARGUMENT_KEY);
        String partyId = bundle.getString("PLAYLIST");
        String userId = bundle.getString("USERID");
        String playlistName = bundle.getString("PLAYLISTNAME");


        mSpotifyService = new SpotifyApi().getService();
        mUserPlaylistPager = new UserPlaylistPager(mSpotifyService, userId, partyId, CredentialsHandler.getToken(mFPlaylistMapper.getContext()));
        setupSearchSongView();

        mFPlaylistMapper.setupToolbarTitle(playlistName);

        search();

    }

    @Override
    public void onSavedState() {

    }

    @Override
    public void onRestoreState() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

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
    public void onQuerySubmit(String query) {
//        search();
    }

    @Override
    public void loadMoreResults() {
        mUserPlaylistPager.getNextPageSearch(mPlaylistCompleteListener);
        Log.d(TAG, "Load more...");
    }

    @Override
    public void search() {
            mFPlaylistMapper.reset();
            mPlaylistCompleteListener = new UserPlaylistPager.CompleteListener() {
                @Override
                public void onComplete(List<Track> items) {
                    addSearchData(items);
                }

                @Override
                public void onError(Throwable error) {
                    logError(error.getMessage());
                }
            };
            mUserPlaylistPager.getFirstPageSearch(PAGE_SIZE, mPlaylistCompleteListener);

    }

    @Override //itemclick
    public void selectTrack(Track item) {
        DJApi.get().addTrackToParty(mParty.getId(), item.id, new retrofit.Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {
                //might do something later
            }

            @Override
            public void failure(RetrofitError error) {
                //failed to add, or host failed to recieve socket message (bad token)
            }
        });
    }

    private void setupSearchSongView() {
        mPlaylistCompleteListener = new UserPlaylistPager.CompleteListener() {
            @Override
            public void onComplete(List<Track> items) {
                mSongListAdapter.addData(items);
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, error.getMessage());
            }
        };

        mSongListAdapter = new SongListAdapter(mFPlaylistMapper.getContext(), (itemView, item) -> selectTrack(item), false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mFPlaylistMapper.getContext());
        mScrollListener = new PlaylistResultScrollListener(mLayoutManager, mUserPlaylistPager, mPlaylistCompleteListener);
        mFPlaylistMapper.setupSearchRecyclerView(mSongListAdapter, mLayoutManager, mScrollListener);
    }

    private void logError(String msg) {
//        Toast.makeText(mSessionActivityMap.getContext(), "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
    }

    private void logMessage(String msg) {
//        Toast.makeText(mSessionActivityMap.getContext(), msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }


//preview player
//            if (currentTrackUrl == null || !currentTrackUrl.equals(previewUrl))
//                mPlayerInt.queueTrack(previewUrl);
//            else if (mPlayerInt.isPlaying()) mPlayerInt.pause();
//            else mPlayerInt.resume();


}
