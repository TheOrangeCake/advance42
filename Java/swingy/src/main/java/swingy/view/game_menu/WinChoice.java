package swingy.view.game_menu;

public enum WinChoice implements Menu {
    TAKE("Equip artifact"),
    DISCARD("Discard artifact");

    private final String description;

    WinChoice(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
