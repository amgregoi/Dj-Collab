package com.teioh08.djcollab.UI.Session.Presenters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.teioh08.djcollab.Models.Party;
import com.teioh08.djcollab.R;
import com.teioh08.djcollab.UI.Session.Adapters.SongListAdapter;
import com.teioh08.djcollab.UI.Session.Views.Fragments.FSearchTrackFragment;
import com.teioh08.djcollab.UI.Session.Views.Maps.FSearchTrackMapper;
import com.teioh08.djcollab.UI.Session.Views.ASessionActivity;
import com.teioh08.djcollab.Utils.SearchPager;
import com.teioh08.djcollab.Webapi.DJApi;
import com.teioh08.djcollab.Widgets.ResultListScrollListener;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class FSearchTrackPresenterImpl implements FSearchTrackPresenter{
    final public static String TAG = FSearchTrackPresenterImpl.class.getSimpleName();
    public static final int PAGE_SIZE = 20;

    private FSearchTrackMapper mFSearchTrackMap;

    public FSearchTrackPresenterImpl(FSearchTrackMapper map) {
        mFSearchTrackMap = map;
    }

    private SearchPager mSearchPager;
    private SearchPager.CompleteListener mSearchCompleteListener;
    private ResultListScrollListener mScrollListener;
    private SongListAdapter mSongListAdapter;
    private String mCurrentQuery;

    private boolean mIsPartyHost;
    private Party mParty;

    private SpotifyService mSpotifyService;

    @Override
    public void init(Bundle bundle) {
        mFSearchTrackMap.setupSearchview();

        mIsPartyHost = bundle.getBoolean(ASessionActivity.ISHOST_ARGUMENT_KEY);
        mParty = bundle.getParcelable(ASessionActivity.PARTY_ARGUMENT_KEY);

        mSpotifyService = new SpotifyApi().getService();
        mSearchPager = new SearchPager(mSpotifyService);
        setupSearchSongView();

        if(bundle.containsKey("PLAYLIST") && bundle.containsKey("USERID")){
            String partyId = bundle.getString("PLAYLIST");
            String userId = bundle.getString("USERID");
            //get playlist tracks
        }else {
            //else something else
        }
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
        search(query);
    }

    @Override
    public void loadMoreResults() {
        mSearchPager.getNextPageSearch(mSearchCompleteListener);
        Log.d(TAG, "Load more...");
    }

    @Override
    public void search(@Nullable String searchQuery) {
        if (searchQuery != null && !searchQuery.isEmpty() && !searchQuery.equals(mCurrentQuery)) {
            logMessage("query text submit " + searchQuery);
            mCurrentQuery = searchQuery;
            mFSearchTrackMap.reset();
            mSearchCompleteListener = new SearchPager.CompleteListener() {
                @Override
                public void onComplete(List<Track> items) {
                    addSearchData(items);
                }

                @Override
                public void onError(Throwable error) {
                    logError(error.getMessage());
                }
            };
            mSearchPager.getFirstPageSearch(searchQuery, PAGE_SIZE, mSearchCompleteListener);
        }
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

        mSongListAdapter = new SongListAdapter(mFSearchTrackMap.getContext(), (itemView, item) -> selectTrack(item), false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mFSearchTrackMap.getContext());
        mScrollListener = new ResultListScrollListener(mLayoutManager, mSearchPager, mSearchCompleteListener);
        mFSearchTrackMap.setupSearchRecyclerView(mSongListAdapter, mLayoutManager, mScrollListener);
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
