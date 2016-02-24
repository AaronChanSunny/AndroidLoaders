package com.aaron.androidloaders.network;

import com.aaron.androidloaders.model.InTheaters;
import com.aaron.androidloaders.model.Subject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Aaron on 2016/2/24.
 */
public interface ApiService {

    @GET("/v2/movie/in_theaters")
    Call<InTheaters> getInTheaters(@Query("city") String city);

    @GET("/v2/movie/subject/{id}")
    Call<Subject> getMovie(@Path("id") String id);
}
