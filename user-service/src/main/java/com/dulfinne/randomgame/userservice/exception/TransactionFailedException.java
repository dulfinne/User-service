package com.dulfinne.randomgame.userservice.exception;

public class TransactionFailedException extends RuntimeException {
  public TransactionFailedException(String message) {
    super(message);
  }
}
