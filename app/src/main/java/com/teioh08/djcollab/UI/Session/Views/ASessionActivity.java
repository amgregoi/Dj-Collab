package com.teioh08.djcollab.UI.Session.Views;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.teioh08.djcollab.Models.Party;
import com.teioh08.djcollab.UI.Session.Adapters.ExpandableListAdapter;
import com.teioh08.djcollab.UI.Session.Views.Fragments.FPlaylistFragment;
import com.teioh08.djcollab.UI.Session.Views.Fragments.FSearchTrackFragment;
import com.teioh08.djcollab.UI.Session.Views.Maps.SessionActivityMap;
import com.teioh08.djcollab.R;
import com.teioh08.djcollab.Utils.SharedPrefsUtil;
import com.teioh08.djcollab.Widgets.PlayListScrollListener;
import com.teioh08.djcollab.UI.Session.Adapters.SongListAdapter;
import com.teioh08.djcollab.UI.Session.Presenters.ASessionPresenter;
import com.teioh08.djcollab.UI.Session.Presenters.ASessionPresenterImpl;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import kaaes.spotify.webapi.android.models.Track;

public class ASessionActivity extends AppCompatActivity implements SessionActivityMap {
    public final static String TAG = ASessionActivity.class.getSimpleName();
    public final static String PARTY_ARGUMENT_KEY = TAG + ":" + "PARTY_KEY";
    public final static String ISHOST_ARGUMENT_KEY = TAG + ":" + "IS_HOST";

    private ASessionPresenter mASessionPresenter;

    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.session_playlist) RecyclerView mPlayList;
    @Bind(R.id.toolbar) Toolbar mToolBar;
    @Bind(R.id.drawer_layout_list) ExpandableListView mDrawerList;
    @Bind(R.id.activityTitle) TextView mActivityTitle;

    private View mDrawerHeader;
    private ExpandableListAdapter adapter;
    private Toast mToast;
    public static Intent constructSessionActivityIntent(Context context, Party party, boolean isHost) {
        Intent argumentIntent = new Intent(context, ASessionActivity.class);
        argumentIntent.putExtra(PARTY_ARGUMENT_KEY, party);
        argumentIntent.putExtra(ISHOST_ARGUMENT_KEY, isHost);
        return argumentIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        ButterKnife.bind(this);

        mToast = Toast.makeText(this, "Press back again to exit party", Toast.LENGTH_SHORT);

        mASessionPresenter = new ASessionPresenterImpl(this);
        mASessionPresenter.init(getIntent().getExtras());
        mASessionPresenter.setupDrawerLayoutListener(mToolBar, mDrawerLayout);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mASessionPresenter.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mASessionPresenter.onPostCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mASessionPresenter.onConfigurationChanged(newConfig);
    }

    @Override
    public void setupPlaylistRecyclerView(SongListAdapter adapter, LinearLayoutManager manager, PlayListScrollListener listener) {
        mPlayList.setLayoutManager(manager);
        mPlayList.setAdapter(adapter);
//        mPlayList.addOnScrollListener(listener);
//        mPlayList.setHasFixedSize(true);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setDrawerLayoutListener(ActionBarDrawerToggle mDrawerToggle) {
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onDrawerOpen() {
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
    }

    @Override
    public void onDrawerClose() {
        invalidateOptionsMenu();  // creates call to onPrepareOptionsMenu()
    }

    @Override
    public void closeDrawer() {
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void addSongQueue(Track track) {
        mASessionPresenter.queueTrack(track.uri);
    }

    @Override
    public void openPlaylistFragment(String playlist, String userid, String playlistName) {
        Fragment playlistFrag = new FPlaylistFragment();
        Bundle bundle = getIntent().getExtras();
        bundle.putString("PLAYLIST", playlist);
        bundle.putString("USERID", userid);
        bundle.putString("PLAYLISTNAME", playlistName);
        playlistFrag.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, playlistFrag, TAG).addToBackStack(TAG).commit();

    }

    @OnClick(R.id.add_song_button)
    void onAddSongButtonClick() {
        Fragment search = new FSearchTrackFragment();
        Bundle bundle = getIntent().getExtras();
        search.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, search, TAG).addToBackStack(TAG).commit();

    }

    @Override
    public void setToolbartitle(String title) {
        mActivityTitle.setText(title);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mASessionPresenter.spotifyAuthenticationResult(requestCode, resultCode, intent);
    }


    @Override
    public void setupDrawerLayout(List<String> mDrawerItems, Map<String, List<String>> mSourceCollections) {
        adapter = new ExpandableListAdapter(this, mDrawerItems, mSourceCollections);
        if (mDrawerHeader != null)
            mDrawerList.removeHeaderView(mDrawerHeader);

        mDrawerHeader = LayoutInflater.from(getContext()).inflate(R.layout.drawer_header, null);
        TextView username = (TextView) mDrawerHeader.findViewById(R.id.drawer_username);
        username.setText(SharedPrefsUtil.getSpotifyUsername());

        mDrawerList.addHeaderView(mDrawerHeader);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            mASessionPresenter.onDrawerItemChosen(groupPosition);
            return false;
        });

        mDrawerList.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            mASessionPresenter.onPlaylistChosen(childPosition);
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!mToast.getView().isShown() && mDrawerLayout.isDrawerOpen(mDrawerList)) { //closes drawer, if exit mToast isn't active
            mDrawerLayout.closeDrawer(mDrawerList);
            mToast.show();
        } else if (!mToast.getView().isShown()) { //opens drawer, and shows exit mToast to verify exit
            mDrawerLayout.openDrawer(mDrawerList);
            mToast.show();
        } else {    //user double back pressed to exit within time frame (mToast length)
            mToast.cancel();
            super.onBackPressed();
        }
    }
}
