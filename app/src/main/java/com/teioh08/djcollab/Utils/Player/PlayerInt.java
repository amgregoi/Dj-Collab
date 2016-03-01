package com.teioh08.djcollab.Utils.Player;


import android.support.annotation.Nullable;

import java.util.List;

public interface PlayerInt {

    void queueTrack(String url);

    void playlist(List<String> url);

    void pause();

    void resume();

    boolean isPlaying();

    @Nullable
    String getCurrentTrack();

    void release();
}
