package com.dulfinne.randomgame.userservice.integration;

import com.dulfinne.randomgame.userservice.dto.request.UserRequest;
import com.dulfinne.randomgame.userservice.dto.response.UserResponse;
import com.dulfinne.randomgame.userservice.entity.User;
import com.dulfinne.randomgame.userservice.repository.UserRepository;
import com.dulfinne.randomgame.userservice.util.ApiPaths;
import com.dulfinne.randomgame.userservice.util.ExceptionKeys;
import com.dulfinne.randomgame.userservice.util.UserTestData;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.hamcrest.Matchers.containsString;
import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class UserServiceIT extends IntegrationTestBase {

  private final UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll().block();
  }

  @Nested
  class GetUser {

    @Test
    void givenExistingUserAuth_whenGetUser_thenReturnUserResponse() {
      userRepository.save(UserTestData.getFirstUser().build()).block();
      UserResponse expected = UserTestData.getFirstUserResponse().build();

      UserResponse result =
          withAuth(UserTestData.FIRST_USERNAME)
              .when()
              .get(ApiPaths.USER_BASE_URL + "/me")
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
          .get(ApiPaths.USER_BASE_URL + "/me")
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
              .post(ApiPaths.USER_BASE_URL)
              .then()
              .statusCode(HttpStatus.CREATED.value())
              .extract()
              .as(UserResponse.class);

      assertThat(result)
          .usingRecursiveComparison()
          .ignoringFields(UserTestData.ID_FIELD)
          .isEqualTo(expected);

      Mono<User> userMono = userRepository.findByUsername(UserTestData.NON_EXISTING_USERNAME);
      StepVerifier.create(userMono)
          .assertNext(
              user -> assertThat(user.getUsername()).isEqualTo(UserTestData.NON_EXISTING_USERNAME))
          .expectComplete()
          .verify();
    }

    @Test
    void givenExistingUserRequest_whenCreateUser_thenReturnErrorResponse() {
      userRepository.save(UserTestData.getFirstUser().build()).block();
      String errorMessage =
          String.format(ExceptionKeys.USER_EXISTS_USERNAME, UserTestData.FIRST_USERNAME);
      UserRequest request = UserTestData.getFirstUserRequest().build();

      withAuth(UserTestData.FIRST_USERNAME)
          .contentType(ContentType.JSON)
          .body(request)
          .when()
          .post(ApiPaths.USER_BASE_URL)
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
      userRepository.save(UserTestData.getFirstUser().build()).block();
      UserRequest request = UserTestData.getUpdateUserRequest().build();
      UserResponse expected =
          UserTestData.getFirstUserResponse()
              .name(UserTestData.SECOND_NAME)
              .surname(UserTestData.SECOND_SURNAME)
              .build();

      UserResponse result =
          withAuth(UserTestData.FIRST_USERNAME)
              .contentType(ContentType.JSON)
              .body(request)
              .when()
              .put(ApiPaths.USER_BASE_URL)
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
          .put(ApiPaths.USER_BASE_URL)
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
      userRepository.save(UserTestData.getFirstUser().build()).block();
      withAuth(UserTestData.FIRST_USERNAME)
          .when()
          .delete(ApiPaths.USER_BASE_URL)
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
          .delete(ApiPaths.USER_BASE_URL)
          .then()
          .statusCode(HttpStatus.NOT_FOUND.value())
          .body("message", containsString(errorMessage))
          .extract();
    }
  }
}
