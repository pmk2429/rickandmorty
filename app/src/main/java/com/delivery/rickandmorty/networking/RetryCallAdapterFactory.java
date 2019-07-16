package com.delivery.rickandmorty.networking;


import com.delivery.rickandmorty.exceptions.TimeoutException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * Retries calls marked with {@link Retry}.
 */
public class RetryCallAdapterFactory extends CallAdapter.Factory {
  private final ScheduledExecutorService mExecutor;

  private RetryCallAdapterFactory() {
    mExecutor = Executors.newScheduledThreadPool(1);
  }

  protected static RetryCallAdapterFactory create() {
    return new RetryCallAdapterFactory();
  }

  @Override
  public CallAdapter<?, ?> get(final Type returnType, Annotation[] annotations, Retrofit retrofit) {
    boolean hasRetryAnnotation = false;
    int value = 0;
    for (Annotation annotation : annotations) {
      if (annotation instanceof Retry) {
        hasRetryAnnotation = true;
        value = ((Retry) annotation).value();
      }
    }
    final boolean shouldRetryCall = hasRetryAnnotation;
    final int maxRetries = value;
    final CallAdapter<?, ?> delegate = retrofit.nextCallAdapter(this, returnType, annotations);

    return new CallAdapter<Object, Object>() {
      @Override
      public Type responseType() {
        return delegate.responseType();
      }

      @Override
      public Object adapt(Call call) {
        return delegate.adapt(shouldRetryCall ? new RetryingCall(call, mExecutor, maxRetries) : call);
      }
    };
  }

  static final class RetryingCall<T> implements Call<T> {
    private final Call<T> mDelegate;
    private final ScheduledExecutorService mExecutor;
    private final int mMaxRetries;

    public RetryingCall(Call<T> delegate, ScheduledExecutorService executor, int maxRetries) {
      mDelegate = delegate;
      mExecutor = executor;
      mMaxRetries = maxRetries;
    }

    @Override
    public Response<T> execute() throws IOException {
      return mDelegate.execute();
    }

    @Override
    public void enqueue(Callback<T> callback) {
      mDelegate.enqueue(new RetryingCallback<>(mDelegate, callback, mExecutor, mMaxRetries));
    }

    @Override
    public boolean isExecuted() {
      return false;
    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isCanceled() {
      return false;
    }

    @SuppressWarnings("CloneDoesntCallSuperClone" /* Performing deep clone */)
    @Override
    public Call<T> clone() {
      return new RetryingCall<>(mDelegate.clone(), mExecutor, mMaxRetries);
    }

    @Override
    public Request request() {
      return null;
    }
  }

  /**
   * Exponential backoff approach from https://developers.google.com/drive/web/handle-errors
   */
  private static final class RetryingCallback<T> implements Callback<T> {
    private static Random random = new Random();
    private final int mMaxRetries;
    private final Call<T> mCall;
    private final Callback<T> mDelegate;
    private final ScheduledExecutorService mExecutor;
    private final int mRetries;

    RetryingCallback(Call<T> call, Callback<T> delegate, ScheduledExecutorService executor, int maxRetries) {
      this(call, delegate, executor, maxRetries, 0);
    }

    RetryingCallback(Call<T> call, Callback<T> delegate, ScheduledExecutorService executor, int maxRetries, int retries) {
      mCall = call;
      mDelegate = delegate;
      mExecutor = executor;
      mMaxRetries = maxRetries;
      mRetries = retries;
    }

    private void retryCall() {
      mExecutor.schedule(new Runnable() {
        @Override
        public void run() {
          final Call<T> call = mCall.clone();
          call.enqueue(new RetryingCallback<>(call, mDelegate, mExecutor, mMaxRetries, mRetries + 1));
        }
      }, (1 << mRetries) * 1000 + random.nextInt(1001), TimeUnit.MILLISECONDS);
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
      mDelegate.onResponse(call, response);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
      // Retry failed request
      if (mRetries < mMaxRetries) {
        retryCall();
      } else {
        mDelegate.onFailure(call, new TimeoutException(t));
      }
    }
  }
}