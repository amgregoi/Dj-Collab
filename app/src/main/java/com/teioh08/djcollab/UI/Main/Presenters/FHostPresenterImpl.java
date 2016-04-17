package com.teioh08.djcollab.UI.Main.Presenters;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.teioh08.djcollab.Webapi.DJApi;
import com.teioh08.djcollab.Models.Party;
import com.teioh08.djcollab.UI.Main.View.AMainActivity;
import com.teioh08.djcollab.UI.Main.View.Fragments.HostFragment;
import com.teioh08.djcollab.UI.Main.View.Mappers.FHostMapper;
import com.teioh08.djcollab.UI.Session.Views.ASessionActivity;

import retrofit.Callback;
import retrofit.RetrofitError;

public class FHostPresenterImpl implements FHostPresenter {
    final public static String TAG = FHostPresenterImpl.class.getSimpleName();

    private FHostMapper mFHostMap;

    public FHostPresenterImpl(FHostMapper map){
        mFHostMap = map;
    }

    @Override
    public void init(Bundle bundle) {
        ((AMainActivity) ((HostFragment)mFHostMap).getActivity()).authenticate();
    }

    @Override
    public void onSavedState() {

    }

    @Override
    public void onRestoreState() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void createPartyButtonClick(String name) {
        name = name.replaceAll("\n", "");
        if(name.length() > 0) {
            DJApi.get().createHostSession(name, new Callback<Party>() {
                @Override
                public void success(Party party, retrofit.client.Response response) {
                    //successfully create host session (party)
                    Intent intent = ASessionActivity.constructSessionActivityIntent(mFHostMap.getContext(), party, true);
                    ((HostFragment) mFHostMap).startActivity(intent);
                }

                @Override
                public void failure(RetrofitError error) {
                    //failed to create host session (party)
                    Log.e(TAG, error.getMessage());
                }
            });
        }else{
            Toast.makeText(mFHostMap.getContext(), "Error: Enter party name", Toast.LENGTH_SHORT).show();
        }
    }
}
