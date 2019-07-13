package com.delivery.rickandmorty.networking;

import com.delivery.rickandmorty.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Creates an actual implementation of the API endpoints specified for an entity. The client abides to singleton
 * design pattern to acquire and open same the socket connection for any request made through out the application.
 */
public class ServiceGenerator {

  /**
   * Generic utility to create any n number of API requests using same Retrofit client instance.
   *
   * @param serviceClass
   * @param <S>
   * @return
   */
  protected static <S> S createService(Class<S> serviceClass) {

    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();

    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            //.addCallAdapterFactory(RetryCallbackAdapter);
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    return retrofit.create(serviceClass);
  }

  private static CharacterApi characterApi = createService(CharacterApi.class);

  public static CharacterApi getCharactersApi() {
    return characterApi;
  }
}
