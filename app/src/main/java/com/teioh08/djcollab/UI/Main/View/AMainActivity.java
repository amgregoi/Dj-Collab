package com.teioh08.djcollab.UI.Main.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import com.teioh08.djcollab.UI.Main.Presenters.AMainPresenter;
import com.teioh08.djcollab.UI.Main.Presenters.AMainPresenterImpl;
import com.teioh08.djcollab.UI.Main.View.Mappers.AMainMapper;
import com.teioh08.djcollab.UI.Main.View.Fragments.HostFragment;
import com.teioh08.djcollab.UI.Main.View.Fragments.JoinFragment;
import com.teioh08.djcollab.R;


import butterknife.ButterKnife;
import butterknife.OnClick;

public class AMainActivity extends AppCompatActivity implements AMainMapper {
    public static final String TAG = AMainActivity.class.getSimpleName();


    private AMainPresenter mAMainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAMainPresenter = new AMainPresenterImpl(this);
    }

    @Override
    public void authenticate() {
        mAMainPresenter.spotifyAuthenticate();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mAMainPresenter.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mAMainPresenter.spotifyAuthenticationResult(requestCode, resultCode, intent);
    }

    @OnClick(R.id.hostButton)
    public void onHostButtonClick() {
        Fragment host = new HostFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, host, TAG).addToBackStack(TAG).commit();
    }

    @OnClick(R.id.joinButton)
    public void onJoinButtonClick() {
        Fragment join = new JoinFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, join, TAG).addToBackStack(TAG).commit();

    }

}
