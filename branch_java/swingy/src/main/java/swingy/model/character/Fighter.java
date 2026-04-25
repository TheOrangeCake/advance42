package swingy.model.character;

public class Fighter extends Hero {
    public static int[] baseStats = {1, 0, 15, 5, 125, 15};

    public Fighter(String name) {
        this.name = name;
        this.className = "Fighter";
        this.attack = Fighter.baseStats[2];
        this.defense = Fighter.baseStats[3];
        this.hitPoints = Fighter.baseStats[4];
        this.crit = Fighter.baseStats[5];
        this.imageUrl = "/assets/fighter.png";
    }
}
