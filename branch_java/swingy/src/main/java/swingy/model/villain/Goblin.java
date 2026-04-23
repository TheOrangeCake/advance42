package swingy.model.villain;

public class Goblin extends Villain {
    public Goblin(int level) {
        this.level = level;
        this.className = "Goblin";
        this.attack = (int) (5 * level * 0.7);
        this.defense = (int) (15 * level * 0.7);
        this.hitPoints = (int) (100 * level * 0.7);
        this.crit = 1;
        this.imageUrl = "/assets/goblin.png";
    }
}
