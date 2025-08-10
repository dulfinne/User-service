package com.dulfinne.randomgame.userservice.util;

public final class ExceptionKeys {
  private ExceptionKeys() {}

  public static final String USER_NOT_FOUND = "User not found: username = %s";
  public static final String USER_EXISTS_USERNAME = "User already exists: username = %s";
  public static final String DEBIT_NOT_ENOUGH_MONEY = "The amount should be less than %s";
}
