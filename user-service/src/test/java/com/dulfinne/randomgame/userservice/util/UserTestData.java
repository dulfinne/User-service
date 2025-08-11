package com.dulfinne.randomgame.userservice.util;

import com.dulfinne.randomgame.userservice.dto.request.UserRequest;
import com.dulfinne.randomgame.userservice.dto.response.UserResponse;
import com.dulfinne.randomgame.userservice.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserTestData {

  public static final String ID_FIELD = "id";

  public static final String NON_EXISTING_USERNAME = "notfound123";
  public static final BigDecimal INITIAL_BALANCE =
      BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);

  public static final String FIRST_ID = "id1";
  public static final String FIRST_USERNAME = "alice123";
  public static final String FIRST_NAME = "Alice";
  public static final String FIRST_SURNAME = "Smith";
  public static final BigDecimal FIRST_BALANCE = new BigDecimal("100.50");

  public static final String SECOND_ID = "id2";
  public static final String SECOND_USERNAME = "bob456";
  public static final String SECOND_NAME = "Bob";
  public static final String SECOND_SURNAME = "Johnson";
  public static final BigDecimal SECOND_BALANCE = new BigDecimal("250.00");

  public static User.UserBuilder getFirstUser() {
    return User.builder()
        .id(FIRST_ID)
        .username(FIRST_USERNAME)
        .name(FIRST_NAME)
        .surname(FIRST_SURNAME)
        .balance(FIRST_BALANCE);
  }

  public static User.UserBuilder getSecondUser() {
    return User.builder()
        .id(SECOND_ID)
        .username(SECOND_USERNAME)
        .name(SECOND_NAME)
        .surname(SECOND_SURNAME)
        .balance(SECOND_BALANCE);
  }

  public static UserRequest.UserRequestBuilder getFirstUserRequest() {
    return UserRequest.builder().name(FIRST_NAME).surname(FIRST_SURNAME);
  }

  public static UserResponse.UserResponseBuilder getFirstUserResponse() {
    return UserResponse.builder()
        .id(FIRST_ID)
        .username(FIRST_USERNAME)
        .name(FIRST_NAME)
        .surname(FIRST_SURNAME)
        .balance(FIRST_BALANCE);
  }
}
