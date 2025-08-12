package com.dulfinne.randomgame.userservice.controller;

import com.dulfinne.randomgame.userservice.dto.request.MoneyRequest;
import com.dulfinne.randomgame.userservice.dto.request.UserRequest;
import com.dulfinne.randomgame.userservice.dto.response.MoneyResponse;
import com.dulfinne.randomgame.userservice.dto.response.UserResponse;
import com.dulfinne.randomgame.userservice.service.UserService;
import com.dulfinne.randomgame.userservice.util.ApiPaths;
import com.dulfinne.randomgame.userservice.util.HeaderConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ApiPaths.USER_BASE_URL)
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping
  public Flux<UserResponse> getUsers(
      @RequestParam(value = "offset", defaultValue = "0") Integer offset,
      @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
    return userService.getUsers(offset, limit);
  }

  @GetMapping(ApiPaths.ME)
  public Mono<UserResponse> getUser(
      @RequestHeader(HeaderConstants.USERNAME_HEADER) String username) {
    return userService.getUser(username);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<UserResponse> createUser(
      @RequestHeader(HeaderConstants.USERNAME_HEADER) String username,
      @RequestBody @Valid UserRequest request) {
    return userService.createUser(username, request);
  }

  @PutMapping
  public Mono<UserResponse> updateUser(
      @RequestHeader(HeaderConstants.USERNAME_HEADER) String username,
      @RequestBody @Valid UserRequest request) {
    return userService.updateUser(username, request);
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteUser(@RequestHeader(HeaderConstants.USERNAME_HEADER) String username) {
    return userService.deleteUser(username);
  }

  @PostMapping(ApiPaths.CREDIT)
  public Mono<UserResponse> creditMoney(
      @RequestHeader(HeaderConstants.USERNAME_HEADER) String username,
      @RequestBody @Valid MoneyRequest request) {
    return userService.creditMoney(username, request);
  }

  @PostMapping(ApiPaths.DEBIT)
  public Mono<UserResponse> debitMoney(
      @RequestHeader(HeaderConstants.USERNAME_HEADER) String username,
      @RequestBody @Valid MoneyRequest request) {
    return userService.debitMoney(username, request);
  }

  @GetMapping(ApiPaths.BALANCE_BY_USERNAME)
  public Mono<MoneyResponse> getBalance(@PathVariable String username) {
    return userService.getBalance(username);
  }
}
