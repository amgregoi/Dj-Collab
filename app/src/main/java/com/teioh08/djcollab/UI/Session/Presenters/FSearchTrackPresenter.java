package com.teioh08.djcollab.UI.Session.Presenters;

import com.teioh08.djcollab.UI.Maps.LifeCycleMapper;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by amgregoi on 2/29/16.
 */
public interface FSearchTrackPresenter extends LifeCycleMapper{
    void search(String searchQuery);

    void resetData();

    void addSearchData(List<Track> items);

    void onQuerySubmit(String query);

    void loadMoreResults();

    void selectTrack(Track item);
}
