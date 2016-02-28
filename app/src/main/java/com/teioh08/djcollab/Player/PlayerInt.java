package com.teioh08.djcollab.Player;


import android.support.annotation.Nullable;

import java.util.List;

public interface PlayerInt {

    void play(String url);

    void playlist(List<String> url);

    void pause();

    void resume();

    boolean isPlaying();

    @Nullable
    String getCurrentTrack();

    void release();
}
