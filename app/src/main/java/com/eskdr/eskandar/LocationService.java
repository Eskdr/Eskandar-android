package com.eskdr.eskandar;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LocationService {

    @Headers("content-type: application/json")
    @POST("location")
    Call<Location> postLocation(@Body Location location);
}
