package swingy.view.game_menu;

public enum SettingMenuChoice implements Menu {
    SWITCH_VIEW("Switch view"),
    BACK("Back");

    private final String description;

    SettingMenuChoice(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}