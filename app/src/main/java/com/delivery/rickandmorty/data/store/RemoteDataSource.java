package com.delivery.rickandmorty.data.store;

import androidx.lifecycle.LiveData;

import com.delivery.rickandmorty.models.RMCharacter;
import com.delivery.rickandmorty.networking.CharacterApiClient;

import java.util.List;

public class RemoteDataSource {
  private static RemoteDataSource sInstance;

  private CharacterApiClient mCharacterApiClient;

  public static RemoteDataSource getInstance() {
    if (sInstance == null) {
      sInstance = new RemoteDataSource();
    }

    return sInstance;
  }

  private RemoteDataSource() {
    mCharacterApiClient = CharacterApiClient.getInstance();
  }

  public void fetchCharactersAsync(String query, int page) {
    mCharacterApiClient.fetchCharactersAsync(query, page);
  }

  public LiveData<List<RMCharacter>> getCharacters() {
    return mCharacterApiClient.getCharacters();
  }

  public void cancelRequest() {
    mCharacterApiClient.cancelOutstandingNetworkRequest();
  }

}
