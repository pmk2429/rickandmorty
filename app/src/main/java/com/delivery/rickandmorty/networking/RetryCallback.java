package com.delivery.rickandmorty.networking;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class RetryCallback<T> implements Callback<T> {

  private int totalRetries = 3;
  private static final String TAG = RetryCallback.class.getSimpleName();
  private final Call<T> call;
  private int retryCount = 0;

  public RetryCallback(Call<T> call, int totalRetries) {
    this.call = call;
    this.totalRetries = totalRetries;
  }

  @Override
  public void onResponse(Call<T> call, Response<T> response) {
    if (!RetryHelper.isCallSuccess(response))
      if (retryCount++ < totalRetries) {
        Log.v(TAG, "Retrying API Call -  (" + retryCount + " / " + totalRetries + ")");
        retry();
      } else
        onFinalResponse(call, response);
    else
      onFinalResponse(call, response);
  }

  @Override
  public void onFailure(Call<T> call, Throwable t) {
    Log.e(TAG, t.getMessage());
    if (retryCount++ < totalRetries) {
      Log.v(TAG, "Retrying API Call -  (" + retryCount + " / " + totalRetries + ")");
      retry();
    } else
      onFinalFailure(call, t);
  }

  public void onFinalResponse(Call<T> call, Response<T> response) {

  }

  public void onFinalFailure(Call<T> call, Throwable t) {
  }

  private void retry() {
    call.clone().enqueue(this);
  }
}