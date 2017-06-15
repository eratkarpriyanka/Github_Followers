package com.ymedialabs.githubfollowers.network;

import com.ymedialabs.githubfollowers.models.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IGithubApi {

    /**
     *
     * @param username
     * @return list of users
     */
    @GET("users/{username}/followers")
    Call<ArrayList<User>> getUserFollowers(@Path("username") String username);

    /**
     *
     * @param username
     * @return user information
     */
    @GET("users/{username}")
    Call<User> getUser(@Path("username") String username);

}
