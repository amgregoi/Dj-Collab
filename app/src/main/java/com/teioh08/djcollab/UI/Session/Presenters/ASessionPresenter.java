package com.teioh08.djcollab.UI.Session.Presenters;


import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.teioh08.djcollab.UI.Maps.LifeCycleMapper;
import com.teioh08.djcollab.Utils.Player.PlayerInt;

import java.util.List;
import java.util.Map;


public interface ASessionPresenter extends PlayerInt, LifeCycleMapper {

    void setupDrawerLayoutListener(Toolbar mToolBar, DrawerLayout mDrawerLayout);

    void onPostCreate();

    void onConfigurationChanged(Configuration newConfig);

    void removeTrack(int pos);

    void refreshPlaylist();


    void spotifyAuthenticationResult(int requestCode, int resultCode, Intent intent);

    void onDrawerItemChosen(int pos);

    void onPlaylistChosen(int pos);


}
