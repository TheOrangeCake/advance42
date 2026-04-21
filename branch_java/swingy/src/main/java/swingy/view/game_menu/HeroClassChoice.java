package swingy.view.game_menu;

import swingy.utils.Colors;

public enum HeroClassChoice implements Menu {
    DEFENDER(Colors.PURPLE_BRIGHT + "Defender" + Colors.RESET + ": He who holds the line. A resilience tanker who isn't afraid of pain!",
            "Defender",
            "/assets/defender.png"),
    FIGHTER(Colors.CYAN_BRIGHT + "Fighter" + Colors.RESET + ": Attack and conquer! A strong swordman who cuts down all foes on his path.",
            "Fighter",
            "/assets/fighter.png");

    private final String description;
    private final String className;
    private final String image;

    HeroClassChoice(String description, String className, String image) {
        this.description = description;
        this.className = className;
        this.image = image;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
    public String getClassName() {
        return this.className;
    }
    public String getImage() {
        return this.image;
    }
}
