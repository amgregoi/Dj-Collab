package com.teioh08.djcollab.Webapi;

import com.teioh08.djcollab.Models.Party;

import java.util.List;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;


public interface DJService {

    @GET("/api/v1/party")
    void getHostList(Callback<List<Party>> callback);

    @PUT("/api/v1/party/create/{name}")
    void createHostSession(@Path("name") String sessionID, Callback<Party> callback);

    @PUT("/api/v1/party/{partyId}/{songId}")
    void addTrackToParty(@Path("partyId") int partyId, @Path("songId") String songid, Callback<Void> callback);

    @POST("/api/v1/host/{hostId}/{partyId}")
    void registerHostToParty(@Path("hostId") int hostid, @Path("partyId") int partyid, Callback<Void> callback);

    @DELETE("/api/v1/party/{partyId}/{songId}")
    void removeTrackFromParty(@Path("partyId") int partyid, @Path("songId") String songid, Callback<Void> callback);

    @GET("/api/v1/party/{partyId}")
    void getParty(@Path("partyId") int partyid, Callback<Party> callback);
}

