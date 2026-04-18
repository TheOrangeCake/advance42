package swingy.utils.game_menu;

public enum SettingMenuChoice implements Menu {
    SWITCH_VIEW("Switch view");

    private final String description;

    SettingMenuChoice(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}