package com.dulfinne.randomgame.userservice.exception;

public class KafkaProcessingException extends RuntimeException {
  public KafkaProcessingException(String message) {
    super(message);
  }
}
