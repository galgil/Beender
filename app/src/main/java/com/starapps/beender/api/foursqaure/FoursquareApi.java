package com.starapps.beender.api.foursqaure;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;



public interface FoursquareApi {

    String BASE_URL = "https://api.foursquare.com/v2/";
    String CLIENT_ID = "E4ZZGGPSWCMI5V01GBNQAB32ES1EVFGPWJNDA03TDBDDV1C5";
    String CLIENT_SECRET = "KCLQ0OGJ5FXH2BKTBO000LJQA0PONDSIZJN3RJXWECOTJYGO";

    @GET("venues/explore")
    Call<Explore> getExplore(@QueryMap Map<String, String> map);
}
