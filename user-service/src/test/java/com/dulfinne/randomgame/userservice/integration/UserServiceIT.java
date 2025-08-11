package com.dulfinne.randomgame.userservice.integration;

import com.dulfinne.randomgame.userservice.dto.request.UserRequest;
import com.dulfinne.randomgame.userservice.dto.response.UserResponse;
import com.dulfinne.randomgame.userservice.entity.User;
import com.dulfinne.randomgame.userservice.repository.UserRepository;
import com.dulfinne.randomgame.userservice.util.ExceptionKeys;
import com.dulfinne.randomgame.userservice.util.HeaderConstants;
import com.dulfinne.randomgame.userservice.util.UserTestData;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class UserServiceIT extends IntegrationTestBase {

  private final UserRepository userRepository;
  @LocalServerPort private int port;

  private final String BASE_URL = "api/v1/users";

  @BeforeEach
  void setUp() {
    userRepository
        .deleteAll()
        .thenMany(
            Flux.just(UserTestData.getFirstUser().build(), UserTestData.getSecondUser().build())
                .flatMap(userRepository::save))
        .blockLast();
  }

  private RequestSpecification withAuth(String username) {
    return given()
        .header(HeaderConstants.USERNAME_HEADER, username)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .port(port);
  }

  @Nested
  class GetUser {
    @Test
    void givenExistingUserAuth_whenGetUser_thenReturnUserResponse() {
      UserResponse expected = UserTestData.getFirstUserResponse().build();

      UserResponse result =
          withAuth(UserTestData.FIRST_USERNAME)
              .when()
              .get(BASE_URL + "/me")
              .then()
              .statusCode(HttpStatus.OK.value())
              .extract()
              .as(UserResponse.class);

      assertThat(result).isEqualTo(expected);
    }

    @Test
    void givenNonExistingUserAuth_whenGetUser_thenReturnErrorResponse() {
      String errorMessage =
          String.format(ExceptionKeys.USER_NOT_FOUND, UserTestData.NON_EXISTING_USERNAME);

      withAuth(UserTestData.NON_EXISTING_USERNAME)
          .when()
          .get(BASE_URL + "/me")
          .then()
          .statusCode(HttpStatus.NOT_FOUND.value())
          .body("message", containsString(errorMessage))
          .extract();
    }
  }

  @Nested
  class CreateUser {

    @Test
    void givenNonExistingUserRequest_whenCreateUser_thenReturnUserResponse() {
      UserRequest request = UserTestData.getFirstUserRequest().build();
      UserResponse expected =
          UserTestData.getFirstUserResponse()
              .username(UserTestData.NON_EXISTING_USERNAME)
              .balance(UserTestData.INITIAL_BALANCE)
              .build();

      UserResponse result =
          withAuth(UserTestData.NON_EXISTING_USERNAME)
              .contentType(ContentType.JSON)
              .body(request)
              .when()
              .post(BASE_URL)
              .then()
              .statusCode(HttpStatus.CREATED.value())
              .extract()
              .as(UserResponse.class);

      assertThat(result)
          .usingRecursiveComparison()
          .ignoringFields(UserTestData.ID_FIELD)
          .isEqualTo(expected);
    }

    @Test
    void givenExistingUserRequest_whenCreateUser_thenReturnErrorResponse() {
      String errorMessage =
          String.format(ExceptionKeys.USER_EXISTS_USERNAME, UserTestData.FIRST_USERNAME);
      UserRequest request = UserTestData.getFirstUserRequest().build();

      withAuth(UserTestData.FIRST_USERNAME)
          .contentType(ContentType.JSON)
          .body(request)
          .when()
          .post(BASE_URL)
          .then()
          .statusCode(HttpStatus.CONFLICT.value())
          .body("message", containsString(errorMessage))
          .extract();
    }
  }

  @Nested
  class UpdateUser {

    @Test
    void givenExistingAuth_whenUpdateUser_thenReturnUserResponse() {
      UserRequest request = UserTestData.getFirstUserRequest().build();
      UserResponse expected =
          UserTestData.getFirstUserResponse()
              .username(UserTestData.SECOND_USERNAME)
              .id(UserTestData.SECOND_ID)
              .balance(UserTestData.SECOND_BALANCE)
              .build();

      UserResponse result =
          withAuth(UserTestData.SECOND_USERNAME)
              .contentType(ContentType.JSON)
              .body(request)
              .when()
              .put(BASE_URL)
              .then()
              .statusCode(HttpStatus.OK.value())
              .extract()
              .as(UserResponse.class);

      assertThat(result).isEqualTo(expected);
    }

    @Test
    void givenNonExistingAuth_whenUpdateUser_thenReturnErrorResponse() {
      String errorMessage =
          String.format(ExceptionKeys.USER_NOT_FOUND, UserTestData.NON_EXISTING_USERNAME);
      UserRequest request = UserTestData.getFirstUserRequest().build();

      withAuth(UserTestData.NON_EXISTING_USERNAME)
          .contentType(ContentType.JSON)
          .body(request)
          .when()
          .put(BASE_URL)
          .then()
          .statusCode(HttpStatus.NOT_FOUND.value())
          .body("message", containsString(errorMessage))
          .extract();
    }
  }

  @Nested
  class DeleteUser {
    @Test
    void givenExistingAuth_whenDeleteUser_thenReturnUserResponse() {
      withAuth(UserTestData.FIRST_USERNAME)
          .when()
          .delete(BASE_URL)
          .then()
          .statusCode(HttpStatus.NO_CONTENT.value());

      Mono<User> userMono = userRepository.findByUsername(UserTestData.FIRST_USERNAME);

      StepVerifier.create(userMono).expectSubscription().expectComplete().verify();
    }

    @Test
    void givenNonExistingAuth_whenDeleteUser_thenReturnUserResponse() {
      String errorMessage =
          String.format(ExceptionKeys.USER_NOT_FOUND, UserTestData.NON_EXISTING_USERNAME);

      withAuth(UserTestData.NON_EXISTING_USERNAME)
          .when()
          .delete(BASE_URL)
          .then()
          .statusCode(HttpStatus.NOT_FOUND.value())
          .body("message", containsString(errorMessage))
          .extract();
    }
  }
}
