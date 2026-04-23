package swingy.model.character;

public class Fighter extends Hero {
    public static int[] baseStats = {1, 0, 30, 10, 100, 5};

    public Fighter(String name) {
        this.name = name;
        this.className = "Fighter";
        this.attack = 30;
        this.defense = 10;
        this.hitPoints = 100;
        this.crit = 5;
        this.imageUrl = "/assets/fighter.png";
    }
}
