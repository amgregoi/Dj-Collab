package com.teioh08.djcollab.UI.Main.View.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.teioh08.djcollab.Models.Party;
import com.teioh08.djcollab.R;
import com.teioh08.djcollab.UI.Main.View.AMainActivity;
import com.teioh08.djcollab.UI.Session.Views.ASessionActivity;
import com.teioh08.djcollab.Webapi.DJApi;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;

public class HostDialog extends DialogFragment {
    final public static String TAG = HostDialog.class.getSimpleName();

    @Bind(R.id.partyName) TextView mUsername;

    private boolean mHosting = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_host_setup, null);
        ButterKnife.bind(this, v);

        ((AMainActivity) getActivity()).authenticate();

        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().requestFeature(Window.FEATURE_SWIPE_TO_DISMISS);

        return v;
    }

    @OnClick(R.id.host_button)
    public void onHostClick() {
        if(mHosting) {
            mHosting = false;
            String name = mUsername.getText().toString();
            name = name.replaceAll("\n", "");
            if (name.length() > 0) {
                DJApi.get().createHostSession(name, new Callback<Party>() {
                    @Override
                    public void success(Party party, retrofit.client.Response response) {
                        //successfully create host session (party)
                        getDialog().dismiss();
                        Intent intent = ASessionActivity.constructSessionActivityIntent(getContext(), party, true);
                        startActivity(intent);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //failed to create host session (party)
                        Log.e(TAG, error.getMessage());
                    }
                });
            } else {
                Toast.makeText(getContext(), "Error: Enter party name", Toast.LENGTH_SHORT).show();
                mHosting = true;
            }
        }
    }

    @OnClick(R.id.cancel_button)
    public void onCancelClick() {
        getDialog().dismiss();
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }
}
