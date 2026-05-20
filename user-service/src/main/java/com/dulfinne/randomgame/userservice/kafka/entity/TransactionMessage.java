package com.dulfinne.randomgame.userservice.kafka.entity;

import com.dulfinne.randomgame.userservice.entity.TransactionType;

import java.math.BigDecimal;

public record TransactionMessage(
    String username,
    BigDecimal amount,
    TransactionType type
) {
}
