package com.dulfinne.randomgame.userservice.controller;

import com.dulfinne.randomgame.userservice.dto.request.ParallelRequest;
import com.dulfinne.randomgame.userservice.util.ApiPaths;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@RestController
@RequestMapping(ApiPaths.PARALLEL_BASE_URL)
public class ParallelController {

  @GetMapping
  public Mono<String> getParallelNonBlocking(@Valid ParallelRequest request) {

    Mono<String> task1 =
        Mono.just("Result 1").delayElement(Duration.ofMillis(request.firstDelay()));

    Mono<String> task2 =
        Mono.just("Result 2").delayElement(Duration.ofMillis(request.secondDelay()));

    return Mono.zip(task1, task2)
        .map(results -> String.format("Results: %s, %s", results.getT1(), results.getT2()));
  }

  @GetMapping(ApiPaths.BLOCKING)
  public Mono<String> getParallelBlocking(@Valid ParallelRequest request) {
    Mono<String> task1 =
        Mono.fromCallable(
                () -> {
                  Thread.sleep(request.firstDelay());
                  return "Result 1";
                })
            .subscribeOn(Schedulers.boundedElastic());

    Mono<String> task2 =
        Mono.fromCallable(
                () -> {
                  Thread.sleep(request.secondDelay());
                  return "Result 2";
                })
            .subscribeOn(Schedulers.boundedElastic());

    return Mono.zip(task1, task2)
        .map(results -> String.format("Results: %s, %s", results.getT1(), results.getT2()));
  }
}
