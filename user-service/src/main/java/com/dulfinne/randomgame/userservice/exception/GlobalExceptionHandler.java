package com.dulfinne.randomgame.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(WebExchangeBindException.class)
  public Mono<Map<String, String>> handleValidationExceptions(WebExchangeBindException ex) {
    Map<String, String> errors = new HashMap<>();
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      String fieldName = fieldError.getField();
      String errorMessage = fieldError.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    }

    return Mono.just(errors);
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler({EntityAlreadyExistsException.class, ActionNotAllowedException.class})
  public Mono<ErrorResponse> handleConflictException(RuntimeException e) {
    return Mono.just(new ErrorResponse(HttpStatus.CONFLICT, e.getMessage()));
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(EntityNotFoundException.class)
  public Mono<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
    return Mono.just(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage()));
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public Mono<ErrorResponse> handleGlobalException(Exception e) {
    return Mono.just(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
  }
}
