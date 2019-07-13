package com.delivery.rickandmorty.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AppExecutors {

  private static AppExecutors sInstance;

  public static AppExecutors getInstance() {
    if (sInstance == null) {
      sInstance = new AppExecutors();
    }
    return sInstance;
  }

  // will be used to handle the Network Timeout scenario
  private final ScheduledExecutorService mNetworkService = Executors.newScheduledThreadPool(3);

  public ScheduledExecutorService networkService() {
    return mNetworkService;
  }
}
