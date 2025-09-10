package com.dulfinne.randomgame.userservice.kafka.entity;

import java.math.BigDecimal;

public record Payment(
   String gameId,
   String username,
   BigDecimal amount,
   Boolean positiveFlag
) {
}
