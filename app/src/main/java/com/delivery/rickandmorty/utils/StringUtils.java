package com.delivery.rickandmorty.utils;

public class StringUtils {

  public static boolean isNullOrBlank(String str) {
    return str == null || str.trim().length() == 0;
  }
}
