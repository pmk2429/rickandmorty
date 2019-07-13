package com.delivery.rickandmorty.networking;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.delivery.rickandmorty.models.RMCharacter;
import com.delivery.rickandmorty.networking.response.ApiResponse;
import com.delivery.rickandmorty.service.AppExecutors;
import com.delivery.rickandmorty.utils.Constants;
import com.delivery.rickandmorty.utils.Converter;
import com.delivery.rickandmorty.utils.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

import static com.delivery.rickandmorty.utils.Constants.NAME_QUERY_PARAM;
import static com.delivery.rickandmorty.utils.Constants.PAGE_PARAM;

public class CharacterApiClient {

  private static final String TAG = "CharacterApiClient";

  private static CharacterApiClient sInstance;

  private MutableLiveData<List<RMCharacter>> mCharactersLiveData;
  private FetchCharactersRunnable mFetchCharactersRunnable;

  public static CharacterApiClient getInstance() {
    if (sInstance == null) {
      sInstance = new CharacterApiClient();
    }
    return sInstance;
  }

  private CharacterApiClient() {
    mCharactersLiveData = new MutableLiveData<>();
  }

  /**
   * The access point for the Repository to get the List of Characters fetched from API by the AppExecutors
   * that is wrapped in LiveData.
   *
   * @return
   */
  public LiveData<List<RMCharacter>> getCharacters() {
    return mCharactersLiveData;
  }

  /**
   * Initializes and consumes the  inorder to make call to the API using FetchCharactersRunnable. Once the list of
   * {@link RMCharacter} have been retrieved, it assigns them to the local LiveData wrapper <code>mCharactersLiveData</code>
   * which is then propagated all the way to UI Controller.
   */
  public void fetchCharactersAsync(String query, int page) {
    if (mFetchCharactersRunnable != null) {
      mFetchCharactersRunnable = null;
    }
    mFetchCharactersRunnable = new FetchCharactersRunnable(query, page);

    final Future handler = AppExecutors.getInstance().networkService().submit(mFetchCharactersRunnable);

    // Set a timeout for the data refresh
    AppExecutors.getInstance().networkService().schedule(() -> {
      // interrupt the background thread from making the request by making user aware that req timed out
      handler.cancel(true);
    }, Constants.NETWORK_TIME_OUT, TimeUnit.MILLISECONDS);
  }

  /**
   * Responsible for calling the Characters API and fetching the data.
   */
  private class FetchCharactersRunnable implements Runnable {

    private int mPage;
    private String mQuery;
    boolean mCancelRequest;

    protected FetchCharactersRunnable(String query, int page) {
      mPage = page;
      mQuery = query;
      mCancelRequest = false;
    }

    @Override
    public void run() {
      // Code that runs in the background thread
      try {
        Response characterApiCallResponse = fetchCharactersFromServer(mQuery, mPage).execute();

        if (mCancelRequest) {
          return;
        }

        // if SUCCESS
        if (characterApiCallResponse.code() == 200) {
          // get list of APICharacters when Life's good
          ApiResponse apiCharacterResponse = ((ApiResponse) characterApiCallResponse.body());

          // Since the Result response from API might contain a lot extra attributes, we are not discarding any but
          // rather converting the response object to a UI compatible light weight and easy to serialize/deserialize
          // RMCharacter object which contains all necessary information to display in UI.
          List<RMCharacter> fetchedCharacters = apiCharacterResponse.getResults().stream()
                  .map(Converter.transform)
                  .collect(Collectors.toList());

          List<RMCharacter> currentCharacters = mCharactersLiveData.getValue();

          if (mPage == 1) {
            mCharactersLiveData.postValue(fetchedCharacters);
          } else {
            currentCharacters.addAll(fetchedCharacters);
            // once the characters are built, post if to LiveData where it would be observed by UI Controller.
            mCharactersLiveData.postValue(currentCharacters);
          }
        } else { // API returned some other status code like 400, 401 etc
          String error = characterApiCallResponse.errorBody().string();
          Log.e(TAG, error);
          // In case of error we will still post NULL to trigger a callback to UI Controller(Activity) from ViewModel
          // and then in UI Controller, we will perform null checks.
          mCharactersLiveData.postValue(null);
        }
      } catch (Exception e) {
        Log.e(TAG, e.getMessage());
        mCharactersLiveData.postValue(null);
      }
    }

    /**
     * Calls the ServiceGenerator to get hold of CharacterApi that'll be used in making an actual API request.
     *
     * @param query Name of the character
     * @param page  page number
     * @return
     */
    private Call<ApiResponse> fetchCharactersFromServer(String query, int page) {
      Map<String, String> queryMap = new LinkedHashMap<>();
      if (!StringUtils.isNullOrBlank(query)) {
        queryMap.put(NAME_QUERY_PARAM, query);
      }
      queryMap.put(PAGE_PARAM, String.valueOf(page));

      return ServiceGenerator.getCharactersApi().getCharacters(queryMap);
    }

    /**
     * Interrupts and cancels the outstanding API network request by interrupting the runnable.
     */
    private void cancelApiRequest() {
      Log.d(TAG, "cancelApiRequest:");
      mCancelRequest = true;
    }
  }

  public void cancelOutstandingNetworkRequest() {
    if (mFetchCharactersRunnable != null) {
      mFetchCharactersRunnable.cancelApiRequest();
    }
  }
}
