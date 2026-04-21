package swingy.view.game_menu;

import swingy.utils.Colors;

public enum HeroClassChoice implements Menu {
    DEFENDER(Colors.PURPLE_BRIGHT + "Defender" + Colors.RESET + ": He who holds the line. A resilience tanker who isn't afraid of pain!"),
    FIGHTER(Colors.CYAN_BRIGHT + "Fighter" + Colors.RESET + ": Attack and conquer! With a sword swing of immense strength, he cuts down all in his path.");

    private final String description;

    HeroClassChoice(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
