package com.dulfinne.randomgame.userservice.aop;

import com.dulfinne.randomgame.userservice.dto.response.UserResponse;
import com.dulfinne.randomgame.userservice.entity.User;
import com.dulfinne.randomgame.userservice.exception.EntityNotFoundException;
import com.dulfinne.randomgame.userservice.mapper.UserMapper;
import com.dulfinne.randomgame.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import com.dulfinne.randomgame.userservice.util.ExceptionKeys;

import static com.dulfinne.randomgame.userservice.util.LoggingUtils.getDifferencesStringForRecord;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LogDifferencesAspect {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Pointcut(
      "@annotation(com.dulfinne.randomgame.userservice.annotation.LogDifferences) && args(username,..)")
  public void anyGreetingMethod(String username) {}

  @Around(value = "anyGreetingMethod(username)", argNames = "pjp,username")
  public Mono<UserResponse> logChanges(ProceedingJoinPoint pjp, String username) throws Throwable {
    Mono<User> oldUserMono = getUserIfExists(username);

    Mono<?> mono = (Mono<?>) pjp.proceed();
    Mono<UserResponse> newUserMono = mono.cast(UserResponse.class);

    return oldUserMono.zipWith(
        newUserMono,
        (oldUser, newUserResponse) -> {
          log.info(
              "Changes detected for user '{}': {}",
              newUserResponse.username(),
              getDifferencesStringForRecord(userMapper.toResponse(oldUser), newUserResponse));
          return newUserResponse;
        });
  }

  private Mono<User> getUserIfExists(String username) {
    return userRepository
        .findByUsername(username)
        .switchIfEmpty(
            Mono.error(
                new EntityNotFoundException(
                    String.format(ExceptionKeys.USER_NOT_FOUND, username))));
  }
}
