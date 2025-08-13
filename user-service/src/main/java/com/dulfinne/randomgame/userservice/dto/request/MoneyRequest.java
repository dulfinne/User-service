package com.dulfinne.randomgame.userservice.dto.request;

import com.dulfinne.randomgame.userservice.util.ValidationKeys;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MoneyRequest(
        @NotNull(message = ValidationKeys.AMOUNT_NOT_NULL)
        @DecimalMin(value = "3", message = ValidationKeys.AMOUNT_MIN)
        @DecimalMax(value = "400", message = ValidationKeys.AMOUNT_MAX)
        BigDecimal amount
) {
}
