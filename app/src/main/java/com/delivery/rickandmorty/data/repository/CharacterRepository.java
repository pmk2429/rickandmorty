package com.delivery.rickandmorty.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.delivery.rickandmorty.data.store.RemoteDataSource;
import com.delivery.rickandmorty.models.RMCharacter;

import java.util.List;

public class CharacterRepository {
  private static CharacterRepository sInstance;
  private RemoteDataSource mRemoteDataSource;

  private int mPage;
  private String mQuery;
  private MutableLiveData<Boolean> mIsNetworkQueryExhausted;
  private MediatorLiveData<List<RMCharacter>> mCharacters;

  public static CharacterRepository getInstance() {
    if (sInstance == null) {
      sInstance = new CharacterRepository();
    }
    return sInstance;
  }

  private CharacterRepository() {
    mRemoteDataSource = RemoteDataSource.getInstance();
    mCharacters = new MediatorLiveData<>();
    mIsNetworkQueryExhausted = new MutableLiveData<>();
    mPage = 1;
    initMediators();
  }

  private void initMediators() {
    LiveData<List<RMCharacter>> characterListApiSource = mRemoteDataSource.getCharacters();
    mCharacters.addSource(characterListApiSource, characters -> {
      if (characters != null) {
        mCharacters.setValue(characters);
        networkCallDone(characters);
      } else {
        // search database
        networkCallDone(null);
      }
    });
  }

  private void networkCallDone(List<RMCharacter> list) {
    if (list != null) {
      if (list.size() % 50 != 0) {
        mIsNetworkQueryExhausted.setValue(true);
      }
    } else {
      mIsNetworkQueryExhausted.setValue(true);
    }
  }

  /**
   * The control which calls the RemoteDataSource to fetch list of {@link RMCharacter} and set the response in LiveData.
   */
  public void fetchCharactersAsync(String query, int page) {
    mIsNetworkQueryExhausted.setValue(false);
    mQuery = query;
    mPage = page;
    mRemoteDataSource.fetchCharactersAsync(query, page);
  }

  /**
   * Makes a call to the RemoteDataSource to get the list of RMCharacters wrapped in LiveData for current limit.
   * For simplicity, we are keeping <code>limit</code> constant at 50 and only incrementing the offset on each call
   * to achieve pagination.
   */
  public void searchNextPage() {
    mPage++;
    fetchCharactersAsync(mQuery, mPage);
  }

  public LiveData<List<RMCharacter>> getCharacters() {
    return mRemoteDataSource.getCharacters();
  }

  public void cancelRequest() {
    mRemoteDataSource.cancelRequest();
  }

  public LiveData<Boolean> isNetworkQueryExhausted() {
    return mIsNetworkQueryExhausted;
  }
}
