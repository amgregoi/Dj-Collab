package com.teioh08.djcollab.UI.Main.Presenters;

import android.content.Intent;

import com.teioh08.djcollab.UI.Maps.LifeCycleMapper;

/**
 * Created by amgregoi on 2/29/16.
 */
public interface AMainPresenter extends LifeCycleMapper {
    void spotifyAuthenticate();

    void spotifyAuthenticationResult(int requestCode, int resultCode, Intent intent);

}
