package swingy.view.game_menu;

public enum InGameSettingChoice implements Menu {
    SWITCH_VIEW("Switch view"),
    SAVE_GAME("Save game"),
    MAIN_MENU("Main menu (save to slot 3)"),
    BACK("Back");

    private final String description;

    InGameSettingChoice(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}