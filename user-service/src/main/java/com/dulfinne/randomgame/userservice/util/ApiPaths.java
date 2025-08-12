package com.dulfinne.randomgame.userservice.util;

public final class ApiPaths {
  private ApiPaths() {}

  public static final String USER_BASE_URL = "/api/v1/users";
  public static final String ME = "/me";
  public static final String DEBIT = "/debit";
  public static final String CREDIT = "/credit";
  public static final String BALANCE_BY_USERNAME = "/{username}/balance";
}
