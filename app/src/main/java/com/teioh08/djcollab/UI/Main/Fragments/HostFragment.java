package com.teioh08.djcollab.UI.Main.Fragments;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.teioh08.djcollab.DJCRest.RestClient;
import com.teioh08.djcollab.Party;
import com.teioh08.djcollab.R;
import com.teioh08.djcollab.UI.Session.Views.SessionActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit2.Call;
import retrofit2.Response;

public class HostFragment extends Fragment {
    final public static String TAG = HostFragment.class.getSimpleName();

    @Bind(R.id.searchView) SearchView mSearchView;
    @Bind(R.id.partyName) TextView mPartyName;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View hostView = inflater.inflate(R.layout.fragment_host_setup, container, false);
        ButterKnife.bind(this, hostView);

        mSearchView.setVisibility(View.GONE);


        return hostView;
    }

    @OnClick(R.id.createButton)
    public void oncreateButtonClick() {
        if(mPartyName.getText().toString().length() > 0) {
            RestClient.get().createHostSession(mPartyName.getText().toString(), new Callback<Party>() {
                @Override
                public void success(Party party, retrofit.client.Response response) {
                    Intent intent = new Intent(getContext(), SessionActivity.class);
                    intent.putExtra("isHost", true);
                    party.setName(mPartyName.getText().toString());
                    intent.putExtra("party", party);
                    startActivity(intent);
                    //successfully create host session
                }

                @Override
                public void failure(RetrofitError error) {
                    error.printStackTrace();
                    //failed to create host session
                }


            });
        }else{
            Toast.makeText(getContext(), "Error: Enter party name", Toast.LENGTH_SHORT).show();
        }
    }

    //save state
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    //restore state
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

}
