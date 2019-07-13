package com.delivery.rickandmorty.networking.response;

import com.google.gson.annotations.SerializedName;

public class Info {

  @SerializedName("count")
  private int count;
  @SerializedName("pages")
  private int pages;
  @SerializedName("next")
  private String next;
  @SerializedName("prev")
  private String prev;

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public int getPages() {
    return pages;
  }

  public void setPages(int pages) {
    this.pages = pages;
  }

  public String getNext() {
    return next;
  }

  public void setNext(String next) {
    this.next = next;
  }

  public String getPrev() {
    return prev;
  }

  public void setPrev(String prev) {
    this.prev = prev;
  }

}