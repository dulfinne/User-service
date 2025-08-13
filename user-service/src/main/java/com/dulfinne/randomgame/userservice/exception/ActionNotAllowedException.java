package com.dulfinne.randomgame.userservice.exception;

public class ActionNotAllowedException extends RuntimeException {
  public ActionNotAllowedException(String message) {
    super(message);
  }
}
