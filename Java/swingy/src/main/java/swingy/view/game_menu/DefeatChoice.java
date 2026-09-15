package swingy.view.game_menu;

public enum DefeatChoice implements Menu {
    MAIN_MENU("Go to main menu"),
    EXIT("Exit to real life");

    private final String description;

    DefeatChoice(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
