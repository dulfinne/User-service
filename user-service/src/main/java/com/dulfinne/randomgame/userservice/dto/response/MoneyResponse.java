package com.dulfinne.randomgame.userservice.dto.response;

import java.math.BigDecimal;

public record MoneyResponse(
        BigDecimal balance
) {
}
