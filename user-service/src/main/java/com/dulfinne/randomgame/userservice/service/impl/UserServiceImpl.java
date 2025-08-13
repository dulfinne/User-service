package com.dulfinne.randomgame.userservice.service.impl;

import com.dulfinne.randomgame.userservice.annotation.LogDifferences;
import com.dulfinne.randomgame.userservice.dto.request.MoneyRequest;
import com.dulfinne.randomgame.userservice.dto.request.UserRequest;
import com.dulfinne.randomgame.userservice.dto.response.MoneyResponse;
import com.dulfinne.randomgame.userservice.dto.response.UserResponse;
import com.dulfinne.randomgame.userservice.entity.User;
import com.dulfinne.randomgame.userservice.exception.ActionNotAllowedException;
import com.dulfinne.randomgame.userservice.exception.EntityAlreadyExistsException;
import com.dulfinne.randomgame.userservice.exception.EntityNotFoundException;
import com.dulfinne.randomgame.userservice.mapper.UserMapper;
import com.dulfinne.randomgame.userservice.repository.UserRepository;
import com.dulfinne.randomgame.userservice.service.UserService;
import com.dulfinne.randomgame.userservice.util.ExceptionKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional(readOnly = true)
  public Mono<List<UserResponse>> getUsers(Integer offset, Integer limit) {
    return userRepository
        .findAllBy(PageRequest.of(offset, limit))
        .map(userMapper::toResponse)
        .collectList();
  }

  @Override
  @Transactional(readOnly = true)
  public Mono<UserResponse> getUser(String username) {
    return getUserIfExists(username).map(userMapper::toResponse);
  }

  @Override
  @Transactional
  public Mono<UserResponse> createUser(String username, UserRequest request) {
    return checkUsernameUniqueness(username)
        .then(Mono.just(userMapper.toEntity(request)))
        .doOnNext(
            user -> {
              user.setUsername(username);
              user.setBalance(BigDecimal.ZERO);
            })
        .flatMap(userRepository::save)
        .map(userMapper::toResponse);
  }

  @Override
  @Transactional
  @LogDifferences
  public Mono<UserResponse> updateUser(String username, UserRequest request) {
    return getUserIfExists(username)
        .doOnNext(user -> userMapper.updateUser(request, user))
        .flatMap(userRepository::save)
        .map(userMapper::toResponse);
  }

  @Override
  @Transactional
  public Mono<Void> deleteUser(String username) {
    return getUserIfExists(username).flatMap(userRepository::delete);
  }

  @Override
  @Transactional
  @LogDifferences
  public Mono<UserResponse> creditMoney(String username, MoneyRequest request) {
    return getUserIfExists(username)
        .doOnNext(
            user -> {
              BigDecimal requestedAmount = request.amount();
              BigDecimal currentBalance = user.getBalance();
              BigDecimal newBalance = currentBalance.add(requestedAmount);

              user.setBalance(newBalance);
            })
        .flatMap(userRepository::save)
        .map(userMapper::toResponse);
  }

  @Override
  @Transactional
  @LogDifferences
  public Mono<UserResponse> debitMoney(String username, MoneyRequest request) {
    return getUserIfExists(username)
        .doOnNext(
            user -> {
              BigDecimal requestedAmount = request.amount();
              BigDecimal currentBalance = user.getBalance();

              checkCanDebit(currentBalance, requestedAmount);

              BigDecimal newBalance = currentBalance.subtract(request.amount());
              user.setBalance(newBalance);
            })
        .flatMap(userRepository::save)
        .map(userMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Mono<MoneyResponse> getBalance(String username) {
    return getUserIfExists(username).map(user -> new MoneyResponse(user.getBalance()));
  }

  private Mono<User> getUserIfExists(String username) {
    return userRepository
        .findByUsername(username)
        .switchIfEmpty(
            Mono.error(
                new EntityNotFoundException(
                    String.format(ExceptionKeys.USER_NOT_FOUND, username))));
  }

  private Mono<Void> checkUsernameUniqueness(String username) {
    return userRepository
        .findByUsername(username)
        .flatMap(
            user ->
                Mono.error(
                    new EntityAlreadyExistsException(
                        String.format(ExceptionKeys.USER_EXISTS_USERNAME, username))))
        .switchIfEmpty(Mono.empty())
        .then();
  }

  private void checkCanDebit(BigDecimal currentBalance, BigDecimal debitingAmount) {
    if (currentBalance.compareTo(debitingAmount) < 0) {
      throw new ActionNotAllowedException(
          String.format(ExceptionKeys.DEBIT_NOT_ENOUGH_MONEY, currentBalance));
    }
  }
}
