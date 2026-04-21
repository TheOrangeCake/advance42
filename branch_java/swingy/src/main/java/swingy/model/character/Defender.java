package swingy.model.character;

import swingy.model.PlayerName;

public class Defender extends Hero {
    public static int[] baseStats = {0, 0, 10, 20, 200, 3};

    public Defender(PlayerName name) {
        this.name = name;
        this.className = "Defender";
        this.level = 0;
        this.experience = 0;
        this.attack = 10;
        this.defense = 20;
        this.hitPoints = 200;
        this.crit = 3;
        this.image = "/assets/defender.png";
    }
}
