package com.teioh08.djcollab.Webapi;


import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;


public interface ExtraSpotifyService {

    //NOTE: I'm using this until they update the release of their spotify api to include this clal

    @GET("/me/playlists")
    void getUserPlaylists(@Header("Authorization") String token, SpotifyCallback<Pager<PlaylistSimple>> callback);

    @GET("/users/{user_id}/playlists/{playlist_id}/tracks")
    void getPlaylistTracks(@Header("Authorization") String token, @Path("user_id") String userId, @Path("playlist_id") String playlistId, SpotifyCallback<Pager<PlaylistTrack>> callback);



}
