package com.dulfinne.randomgame.userservice.service;

import com.dulfinne.randomgame.userservice.dto.request.MoneyRequest;
import com.dulfinne.randomgame.userservice.dto.request.UserRequest;
import com.dulfinne.randomgame.userservice.dto.response.MoneyResponse;
import com.dulfinne.randomgame.userservice.dto.response.UserResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
  Flux<UserResponse> getUsers(Integer offset, Integer limit);

  Mono<UserResponse> getUser(String username);

  Mono<UserResponse> createUser(String username, UserRequest request);

  Mono<UserResponse> updateUser(String username, UserRequest request);

  Mono<Void> deleteUser(String username);

  Mono<UserResponse> creditMoney(String username, MoneyRequest request);

  Mono<UserResponse> debitMoney(String username, MoneyRequest request);

  Mono<MoneyResponse> getBalance(String username);
}
