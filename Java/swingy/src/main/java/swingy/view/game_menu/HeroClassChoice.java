package swingy.view.game_menu;

import swingy.utils.Colors;
import swingy.model.character.Defender;
import swingy.model.character.Fighter;

public enum HeroClassChoice implements Menu {
    DEFENDER(Colors.PURPLE_BRIGHT + "DEFENDER" + Colors.RESET + "\t- " +
            Colors.BLUE + "Lvl: " + Colors.RESET +
            Defender.baseStats[0] + ", " +
            Colors.BLUE + "Exp: " + Colors.RESET +
            Defender.baseStats[1] +  ", " +
            Colors.BLUE + "Atk: " + Colors.RESET +
            Defender.baseStats[2] +  ", " +
            Colors.BLUE + "Def: " + Colors.RESET +
            Defender.baseStats[3] +  ", " +
            Colors.BLUE + "Hp: " + Colors.RESET +
            Defender.baseStats[4] +  ", " +
            Colors.BLUE + "Crit: " + Colors.RESET +
            Defender.baseStats[5],
            "Defender",
            "/assets/defender.png",
            Defender.baseStats),
    FIGHTER(Colors.CYAN_BRIGHT + "FIGHTER" + Colors.RESET + "\t- " +
            Colors.BLUE + "Lvl: " + Colors.RESET +
            Fighter.baseStats[0] +  ", " +
            Colors.BLUE + "Exp: " + Colors.RESET +
            Fighter.baseStats[1] +  ", " +
            Colors.BLUE + "Atk: " + Colors.RESET +
            Fighter.baseStats[2] +  ", " +
            Colors.BLUE + "Def: " + Colors.RESET +
            Fighter.baseStats[3] +  ", " +
            Colors.BLUE + "Hp: " + Colors.RESET +
            Fighter.baseStats[4] +  ", " +
            Colors.BLUE + "Crit: " + Colors.RESET +
            Fighter.baseStats[5],
            "Fighter",
            "/assets/fighter.png",
            Fighter.baseStats);

    private final String description;
    private final String className;
    private final String image;
    private final int[] baseStats;

    HeroClassChoice(String description, String className, String image, int[] baseStats) {
        this.description = description;
        this.className = className;
        this.image = image;
        this.baseStats = baseStats;
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
    public int[] getBaseStats() {
        return this.baseStats;
    }
}
