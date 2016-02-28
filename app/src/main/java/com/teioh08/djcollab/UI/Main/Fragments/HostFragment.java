package com.teioh08.djcollab.UI.Main.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toolbar;

import com.teioh08.djcollab.R;
import com.teioh08.djcollab.UI.Session.Views.SessionActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HostFragment extends Fragment {

    @Bind(R.id.searchView) SearchView mSearchView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View hostView = inflater.inflate(R.layout.fragment_host_setup, container, false);
        ButterKnife.bind(this, hostView);

        mSearchView.setVisibility(View.GONE);
        return hostView;
    }

    @OnClick(R.id.createButton)
    public void  oncreateButtonClick(){
        Intent intent = new Intent(getContext(), SessionActivity.class);
        startActivity(intent);
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

}
