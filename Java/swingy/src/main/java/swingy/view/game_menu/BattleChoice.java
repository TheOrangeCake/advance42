package swingy.view.game_menu;

public enum BattleChoice implements Menu {
    ATTACK("Attack"),
    RUN("Run");

    private final String description;

    BattleChoice(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
