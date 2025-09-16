package com.dulfinne.randomgame.userservice.kafka.entity;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Payment(
   String gameId,
   String username,
   BigDecimal amount,
   Boolean positiveFlag
) {
}
