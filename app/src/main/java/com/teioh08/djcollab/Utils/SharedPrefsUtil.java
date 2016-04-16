package com.teioh08.djcollab.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.teioh08.djcollab.DJApplication;
import com.teioh08.djcollab.R;


public class SharedPrefsUtil {


    public static void initializePreferences(){
//        Context context = DJApplication.getInstance();

    }

    /**
     * Sets the users MyAnimeList(MAL) login credentials for authorized API calls
     *
     * @param username, The users MyAnimeList Username
     */
    public static void setSpotifyUsername(String username){
        Context context = DJApplication.getInstance();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(context.getString(R.string.PREF_SPOTIFY_USERNAME), username);
        editor.apply();
    }

    /**
     * Get the users MAL username
     *
     * @return The users MAL username
     */
    public static String getSpotifyUsername(){
        Context context = DJApplication.getInstance();
        return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.PREF_SPOTIFY_USERNAME), "Guest (Sign in)");
    }

}
