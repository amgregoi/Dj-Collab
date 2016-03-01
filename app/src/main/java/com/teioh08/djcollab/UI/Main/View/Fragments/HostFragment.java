package com.teioh08.djcollab.UI.Main.View.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teioh08.djcollab.R;
import com.teioh08.djcollab.UI.Main.Presenters.FHostPresenter;
import com.teioh08.djcollab.UI.Main.Presenters.FHostPresenterImpl;
import com.teioh08.djcollab.UI.Main.View.Mappers.FHostMapper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HostFragment extends Fragment implements FHostMapper {
    final public static String TAG = HostFragment.class.getSimpleName();

    private FHostPresenter mFHostPresenter;
    @Bind(R.id.partyName) TextView mPartyName;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View hostView = inflater.inflate(R.layout.fragment_host_setup, container, false);
        ButterKnife.bind(this, hostView);

        mFHostPresenter = new FHostPresenterImpl(this);
        mFHostPresenter.init(getArguments());

        return hostView;
    }

    @OnClick(R.id.createButton)
    public void oncreateButtonClick() {
        mFHostPresenter.createPartyButtonClick(mPartyName.getText().toString());
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

}
