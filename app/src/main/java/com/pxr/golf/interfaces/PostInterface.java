package com.pxr.golf.interfaces;

import com.pxr.golf.models.Post;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface PostInterface {
    @Headers({"x-ig-app-id: 936619743392459"})
    @GET("users/web_profile_info")
    Call<Post> getPosts(@Query("username") String username);
}
