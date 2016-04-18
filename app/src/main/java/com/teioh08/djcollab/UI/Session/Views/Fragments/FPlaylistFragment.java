package com.teioh08.djcollab.UI.Session.Views.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.teioh08.djcollab.R;
import com.teioh08.djcollab.UI.Session.Adapters.SongListAdapter;
import com.teioh08.djcollab.UI.Session.Presenters.FPlaylistPresenter;
import com.teioh08.djcollab.UI.Session.Presenters.FPlaylistPresenterImpl;
import com.teioh08.djcollab.UI.Session.Views.Maps.FPlaylistMapper;
import com.teioh08.djcollab.Widgets.PlaylistResultScrollListener;

import butterknife.Bind;
import butterknife.ButterKnife;


public class FPlaylistFragment extends Fragment implements FPlaylistMapper {
    final public static String TAG = FPlaylistFragment.class.getSimpleName();


    @Bind(R.id.search_song) RecyclerView mSearchTrackView;
    @Bind(R.id.searchView) SearchView mSearchView;
    @Bind(R.id.activityTitle) TextView mActivityTitle;
    @Bind(R.id.toolbar) Toolbar mToolbar;

    FPlaylistPresenter mFPlaylistPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View searchView = inflater.inflate(R.layout.fragment_search_track, container, false);
        ButterKnife.bind(this, searchView);

        mFPlaylistPresenter = new FPlaylistPresenterImpl(this);
        mFPlaylistPresenter.init(getArguments());

        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

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
        mSearchView.clearFocus();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void setupSearchRecyclerView(SongListAdapter adapter, LinearLayoutManager manager, PlaylistResultScrollListener listener) {
        mSearchTrackView.setLayoutManager(manager);
        mSearchTrackView.setAdapter(adapter);
        mSearchTrackView.addOnScrollListener(listener);
        mSearchTrackView.setHasFixedSize(true);
    }

    @Override
    public void reset() {
        mFPlaylistPresenter.resetData();
    }

    @Override
    public void setupSearchview() {
        mSearchView.setOnQueryTextListener(this);

        mSearchView.setOnQueryTextFocusChangeListener((view, queryTextFocused) -> {
            if(getContext() != null) {
                if (!queryTextFocused) {
                    mActivityTitle.setVisibility(View.VISIBLE);
                    mSearchView.setIconified(true);
                    mSearchView.setQuery("", true);
                } else {
                    mActivityTitle.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void setupToolbarTitle(String name) {
        mActivityTitle.setText(name);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        mFPlaylistPresenter.onQuerySubmit(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }
}
