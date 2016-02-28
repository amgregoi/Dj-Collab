package com.teioh08.djcollab.Player;


import android.support.annotation.Nullable;

public interface PlayerInt {

    void play(String url);

    void pause();

    void resume();

    boolean isPlaying();

    @Nullable
    String getCurrentTrack();

    void release();
}
