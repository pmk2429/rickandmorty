package com.delivery.rickandmorty.networking;

import com.delivery.rickandmorty.networking.response.ApiResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface CharacterApi {

  @Retry
  @GET(".")
  Call<ApiResponse> getCharacters(@QueryMap Map<String, String> options);

  @Retry(2)
  @GET("/{id}/")
  Call<ApiResponse> getCharacterById(@Path("id") Integer id);
}
