package com.dulfinne.randomgame.userservice.dto.request;

import com.dulfinne.randomgame.userservice.util.ValidationKeys;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
    @NotBlank(message = ValidationKeys.NAME_NOT_BLANK)
    String name,

    @NotBlank(message = ValidationKeys.SURNAME_NOT_BLANK)
    String surname
) {
}
