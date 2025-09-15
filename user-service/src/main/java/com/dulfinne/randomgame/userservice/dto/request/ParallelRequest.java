package com.dulfinne.randomgame.userservice.dto.request;

import jakarta.validation.constraints.Positive;

public record ParallelRequest(
  @Positive
  Long firstDelay,

  @Positive
  Long secondDelay
) {
}
