package com.delivery.rickandmorty.exceptions;

import java.io.IOException;

public class TimeoutException extends IOException {
  private static final long serialVersionUID = -6469766654369165864L;

  public TimeoutException() {
    super();
  }

  public TimeoutException(Throwable cause) {
    super(cause);
  }
}
