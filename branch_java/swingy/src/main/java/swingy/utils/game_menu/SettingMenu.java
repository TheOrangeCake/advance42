package swingy.utils.game_menu;

public enum SettingMenu implements Menu {
    SWITCH_VIEW("Switch view"),
    BACK("Back");

    private final String description;

    SettingMenu(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}