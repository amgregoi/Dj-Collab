package com.teioh08.djcollab.UI.Main.View.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.teioh08.djcollab.R;
import com.teioh08.djcollab.UI.Main.Presenters.FJoinPresenter;
import com.teioh08.djcollab.UI.Main.Presenters.FJoinPresenterImpl;
import com.teioh08.djcollab.UI.Main.View.Mappers.FJoinMapper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class JoinFragment extends Fragment implements FJoinMapper{
    public static final String TAG  = JoinFragment.class.getSimpleName();


    @Bind(R.id.sessionListView) ListView mSessionList;

    private FJoinPresenter mFjoinPresenter;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View joinView = inflater.inflate(R.layout.fragment_join_session, container, false);
        ButterKnife.bind(this, joinView);

        mFjoinPresenter = new FJoinPresenterImpl(this);
        return joinView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //TODO savestate
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //TODO restorestate
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mFjoinPresenter.onResume();
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
        mFjoinPresenter.onHostItemClicked(pos);
    }

    @Override
    public void registerAdapter(ArrayAdapter adapter) {
        mSessionList.setAdapter(adapter);
    }

    @Override
    public void setupToolBar(){
    }
}
