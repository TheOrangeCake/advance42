package swingy.model.character;

public class Defender extends Hero {
    public static int[] baseStats = {1, 0, 5, 15, 200, 5};

    public Defender(String name) {
        this.name = name;
        this.className = "Defender";
        this.attack = Defender.baseStats[2];
        this.defense = Defender.baseStats[3];
        this.hitPoints = Defender.baseStats[4];
        this.crit = Defender.baseStats[5];
        this.imageUrl = "/assets/defender.png";
    }
}
