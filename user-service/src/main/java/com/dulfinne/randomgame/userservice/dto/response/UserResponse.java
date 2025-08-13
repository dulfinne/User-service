package com.dulfinne.randomgame.userservice.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UserResponse(
    String id,
    String username,
    String name,
    String surname,
    BigDecimal balance
) {
}
