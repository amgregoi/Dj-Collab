package com.teioh08.djcollab.UI.Main.Presenters;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.teioh08.djcollab.UI.Session.Views.ASessionActivity;
import com.teioh08.djcollab.Webapi.DJApi;
import com.teioh08.djcollab.Models.Party;
import com.teioh08.djcollab.R;
import com.teioh08.djcollab.UI.Main.Adapters.SessionListAdapter;
import com.teioh08.djcollab.UI.Main.View.Fragments.JoinFragment;
import com.teioh08.djcollab.UI.Main.View.Mappers.FJoinMapper;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;

public class FJoinPresenterImpl implements  FJoinPresenter{
    final public static String TAG = FJoinPresenterImpl.class.getSimpleName();

    private SessionListAdapter mAdapter;
    private ArrayList<Party> mPartyList;
    private FJoinMapper mFJoinMap;

    public FJoinPresenterImpl(FJoinMapper map){
        mFJoinMap = map;
    }

    @Override
    public void init(Bundle bundle) {
        mFJoinMap.setupToolBar();
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
        DJApi.get().getHostList(new Callback<List<Party>>() {
            @Override
            public void success(List<Party> call, retrofit.client.Response response) {
                if(mFJoinMap.getContext() != null) {
                    mPartyList = new ArrayList<Party>(call);
                    mAdapter = new SessionListAdapter(mFJoinMap.getContext(), R.layout.join_session_list_item, mPartyList);
                    mFJoinMap.registerAdapter(mAdapter);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Failed to retrive party list : " + error.getMessage());

                //todo let user know it failed, refresh option view
                mPartyList = new ArrayList<Party>();
                mAdapter = new SessionListAdapter(mFJoinMap.getContext(), R.layout.join_session_list_item, mPartyList);
                mFJoinMap.registerAdapter(mAdapter);

            }
        });
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onHostItemClicked(int pos) {
        Intent intent = ASessionActivity.constructSessionActivityIntent(mFJoinMap.getContext(), mPartyList.get(pos), false);
        ((JoinFragment)mFJoinMap).startActivity(intent);
    }
}
