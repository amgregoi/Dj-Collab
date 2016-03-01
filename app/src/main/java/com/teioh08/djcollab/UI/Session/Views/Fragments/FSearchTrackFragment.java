package com.teioh08.djcollab.UI.Session.Views.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.teioh08.djcollab.R;
import com.teioh08.djcollab.UI.Session.Adapters.SongListAdapter;
import com.teioh08.djcollab.UI.Session.Views.Maps.FSearchTrackMapper;
import com.teioh08.djcollab.UI.Session.Presenters.FSearchTrackPresenter;
import com.teioh08.djcollab.UI.Session.Presenters.FSearchTrackPresenterImpl;
import com.teioh08.djcollab.Widgets.PlayListScrollListener;
import com.teioh08.djcollab.Widgets.ResultListScrollListener;

import butterknife.Bind;
import butterknife.ButterKnife;


public class FSearchTrackFragment extends Fragment implements FSearchTrackMapper {
    final public static String TAG = FSearchTrackFragment.class.getSimpleName();


    @Bind(R.id.search_song) RecyclerView mSearchTrackView;
    @Bind(R.id.searchView) SearchView mSearchView;
    @Bind(R.id.activityTitle) TextView mActivityTitle;

    FSearchTrackPresenter mFSearchTrackPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View searchView = inflater.inflate(R.layout.fragment_search_track, container, false);
        ButterKnife.bind(this, searchView);

        mFSearchTrackPresenter = new FSearchTrackPresenterImpl(this);
        mFSearchTrackPresenter.init(getArguments());
        return searchView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //TODO: save state
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //TODO: restore state
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

    @Override
    public void setupSearchRecyclerView(SongListAdapter adapter, LinearLayoutManager manager, ResultListScrollListener listener) {
        mSearchTrackView.setLayoutManager(manager);
        mSearchTrackView.setAdapter(adapter);
        mSearchTrackView.addOnScrollListener(listener);
        mSearchTrackView.setHasFixedSize(true);
    }

    @Override
    public void reset() {
        mFSearchTrackPresenter.resetData();
    }

    @Override
    public void setupSearchview() {
        mSearchView.setOnQueryTextListener(this);

        mSearchView.setOnQueryTextFocusChangeListener((view, queryTextFocused) -> {
            if (!queryTextFocused) {
                mActivityTitle.setVisibility(View.VISIBLE);
                mSearchView.setIconified(true);
                mSearchView.setQuery("", true);
            } else {
                mActivityTitle.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        mFSearchTrackPresenter.onQuerySubmit(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }
}
