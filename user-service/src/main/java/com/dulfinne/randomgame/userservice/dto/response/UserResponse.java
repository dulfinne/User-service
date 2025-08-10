package com.dulfinne.randomgame.userservice.dto.response;

import java.math.BigDecimal;

public record UserResponse(
    String id,
    String username,
    String name,
    String surname,
    BigDecimal balance
) {
}
