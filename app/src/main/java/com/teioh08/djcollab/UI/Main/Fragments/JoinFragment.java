package com.teioh08.djcollab.UI.Main.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.teioh08.djcollab.DJCRest.RestClient;
import com.teioh08.djcollab.Party;
import com.teioh08.djcollab.UI.Main.Adapters.SessionListAdapter;
import com.teioh08.djcollab.R;
import com.teioh08.djcollab.UI.Session.Views.SessionActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import retrofit.Callback;
import retrofit.RetrofitError;

public class JoinFragment extends Fragment {
    public static final String TAG  = JoinFragment.class.getSimpleName();

    private SessionListAdapter mAdapter;
    private ArrayList<Party> mPartyList;

    @Bind(R.id.sessionListView) ListView mSessionList;
    @Bind(R.id.searchView) SearchView mSearchView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View joinView = inflater.inflate(R.layout.fragment_join_session, container, false);
        ButterKnife.bind(this, joinView);

        RestClient.get().getHostList(new Callback<List<Party>>() {
            @Override
            public void success(List<Party> call, retrofit.client.Response response) {
                if(call != null) mPartyList = new ArrayList<Party>(call);
                else mPartyList = new ArrayList<Party>();
                mAdapter = new SessionListAdapter(getContext(), R.layout.join_session_list_item, mPartyList);
                mSessionList.setAdapter(mAdapter);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Failed to retrive party list : " + error.getMessage());
                mPartyList = new ArrayList<Party>();
                mAdapter = new SessionListAdapter(getContext(), R.layout.join_session_list_item, mPartyList);
                mSessionList.setAdapter(mAdapter);

            }

        });

        mSearchView.setVisibility(View.GONE);
        return joinView;
    }

    //save state
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    //restore state
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }


    @OnItemClick(R.id.sessionListView)
    void onItemClick(AdapterView<?> adapter, View view, int pos) {
        Intent intent = new Intent(getContext(), SessionActivity.class);
        //add arguments to intent
        intent.putExtra("party", mPartyList.get(pos));
        intent.putExtra("isHost", false);
        startActivity(intent);

    }
}
