package com.delivery.rickandmorty.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.delivery.rickandmorty.data.repository.CharacterRepository;
import com.delivery.rickandmorty.models.RMCharacter;

import java.util.List;

public class CharacterListViewModel extends ViewModel {
  private static final String TAG = "CharacterListViewModel";

  private CharacterRepository mCharacterRepository;
  private boolean isViewingCharacters;
  private boolean mIsMakingNetworkCall;

  public CharacterListViewModel() {
    mIsMakingNetworkCall = false;
    mCharacterRepository = CharacterRepository.getInstance();
  }

  public LiveData<List<RMCharacter>> getCharacters() {
    return mCharacterRepository.getCharacters();
  }

  private void fetchCharacters(String query, int page) {
    isViewingCharacters = true;
    mIsMakingNetworkCall = true;
    mCharacterRepository.fetchCharactersAsync(query, page);
  }

  /**
   * Fetches list of all Characters from server with pagination.
   */
  public void fetchAllCharacters(int page) {
    fetchCharacters(null, page);
  }

  public void searchCharactersByName(String query, int page) {
    fetchCharacters(query, page);
  }

  public void setIsMakingNetworkCall(boolean isMakingNetworkCall) {
    mIsMakingNetworkCall = isMakingNetworkCall;
  }

  public boolean isViewingCharacters() {
    return isViewingCharacters;
  }

  public void setIsViewingCharacters(boolean isViewingCharacters) {
    this.isViewingCharacters = isViewingCharacters;
  }

  public boolean isMakingNetworkCall() {
    return mIsMakingNetworkCall;
  }

  public boolean onBackPressed() {
    if (mIsMakingNetworkCall) {
      // cancel the query
      mCharacterRepository.cancelRequest();
      mIsMakingNetworkCall = false;
    }

    if (isViewingCharacters) {
      isViewingCharacters = false;
      return false;
    }
    return true;
  }

  public void searchNextPage() {
    Log.d(TAG, "searchNextPage: called.");
    if (!mIsMakingNetworkCall && isViewingCharacters && !isNetworkQueryExhausted().getValue()) {
      mCharacterRepository.searchNextPage();
    }
  }

  public LiveData<Boolean> isNetworkQueryExhausted() {
    return mCharacterRepository.isNetworkQueryExhausted();
  }
}
