package swingy.view.game_menu;

public enum InGameChoice implements Menu {
    UP("Up"),
    DOWN("Down"),
    LEFT("Left"),
    RIGHT("Right"),
    SETTING("Setting");

    private final String description;

    InGameChoice(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}