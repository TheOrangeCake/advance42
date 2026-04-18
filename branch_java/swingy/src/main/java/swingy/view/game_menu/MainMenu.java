package swingy.view.game_menu;

public enum MainMenu implements Menu {
    NEW_GAME("Start a new adventure"),
    CONTINUE_SAVE("Resume an adventure"),
    SETTING("Setting"),
    EXIT("Exit to real life");

    private final String description;
    MainMenu(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
