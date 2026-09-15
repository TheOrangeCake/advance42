package swingy.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record PlayerName(
        @NotBlank
        @Pattern(regexp = "[a-zA-Z0-9]+")
        @Size(min = 2, max = 20)
        String playerName
) {
    public String getPlayerName() {
        return this.playerName;
    }
}
