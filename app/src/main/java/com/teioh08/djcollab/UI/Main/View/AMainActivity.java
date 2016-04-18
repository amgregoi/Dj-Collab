package com.teioh08.djcollab.UI.Main.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.teioh08.djcollab.UI.Main.Presenters.AMainPresenter;
import com.teioh08.djcollab.UI.Main.Presenters.AMainPresenterImpl;
import com.teioh08.djcollab.UI.Main.View.Mappers.AMainMapper;
import com.teioh08.djcollab.UI.Main.View.Fragments.HostDialog;
import com.teioh08.djcollab.UI.Main.View.Fragments.JoinFragment;
import com.teioh08.djcollab.R;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

public class AMainActivity extends AppCompatActivity implements AMainMapper {
    public static final String TAG = AMainActivity.class.getSimpleName();

    @Bind(R.id.toolbar) Toolbar mToolbar;

    private AMainPresenter mAMainPresenter;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mToast = Toast.makeText(this, "Press back again to exit party", Toast.LENGTH_SHORT);
        mAMainPresenter = new AMainPresenterImpl(this);
        mAMainPresenter.init(getIntent().getExtras());
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
//        toggleNavIcon();
//        mToolbar.setNavigationIcon(R.drawable.ic_back);
        DialogFragment host = new HostDialog();
        host.show(getSupportFragmentManager(), HostDialog.TAG);
//        getSupportFragmentManager().beginTransaction().add(R.id.container, host, TAG).addToBackStack(TAG).commit();
    }

    @OnClick(R.id.joinButton)
    public void onJoinButtonClick() {
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        Fragment join = new JoinFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, join, TAG).addToBackStack(TAG).commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            mToolbar.setNavigationIcon(R.drawable.ic_music_note_white_24dp);
        } else if (!mToast.getView().isShown()) { //opens drawer, and shows exit mToast to verify exit
            mToast.show();
        } else {    //user double back pressed to exit within time frame (mToast length)
            mToast.cancel();
            super.onBackPressed();
        }
    }

    @Override
    public void setupToolBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        mToolbar.setNavigationIcon(R.drawable.ic_music_note_white_24dp);
        mToolbar.setNavigationOnClickListener(v -> {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                AMainActivity.this.onBackPressed();
        });
    }

    @Override
    public void toggleNavIcon() {
    }
}
