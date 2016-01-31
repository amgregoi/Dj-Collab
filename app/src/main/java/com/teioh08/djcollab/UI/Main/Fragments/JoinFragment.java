package com.teioh08.djcollab.UI.Main.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.teioh08.djcollab.UI.Main.Adapters.SessionListAdapter;
import com.teioh08.djcollab.R;
import com.teioh08.djcollab.UI.Session.Views.SessionActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class JoinFragment extends Fragment {
    public static final String TAG  = JoinFragment.class.getSimpleName();

    private SessionListAdapter adapter;
    private ArrayList<String> things;

    @Bind(R.id.sessionListView) ListView mSessionList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View joinView = inflater.inflate(R.layout.fragment_join_session, container, false);
        ButterKnife.bind(this, joinView);
        things = new ArrayList<>();
        things.add("HARHAR");
        things.add("I'm a lumber");
        things.add("jack");
        adapter = new SessionListAdapter(getContext(), R.layout.join_session_list_item, things);

        mSessionList.setAdapter(adapter);

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
        startActivity(intent);

    }
}
