package swingy.view.game_menu;

public enum InGameChoice implements Menu {
    UP("North"),
    DOWN("South"),
    LEFT("West"),
    RIGHT("East"),
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