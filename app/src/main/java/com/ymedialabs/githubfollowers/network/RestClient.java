package com.ymedialabs.githubfollowers.network;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static final String BASE_URL = "https://api.github.com";

    public RestClient(){

    }

    public IGithubApi createService(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        IGithubApi apiInterface = retrofit.create(IGithubApi.class);
        return apiInterface;
    }    
}
