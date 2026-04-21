package swingy.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record ConsoleChoice(
        @NotNull
        @Min(value = 1, message = "Choice must be at least 1")
        int choice
) {
}