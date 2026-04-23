package swingy.model.villain;

public class Skeleton extends Villain {
    public Skeleton(int level) {
        this.level = level;
        this.className = "Skeleton";
        this.attack = (int) (10 * level * 0.7);
        this.defense = (int) (10 * level * 0.7);
        this.hitPoints = (int) (75 * level * 0.7);
        this.crit = 1;
        this.imageUrl = "/assets/skeleton.png";
    }
}
