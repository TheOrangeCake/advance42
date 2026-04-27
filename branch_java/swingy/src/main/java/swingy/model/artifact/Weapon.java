package swingy.model.artifact;

import swingy.utils.RandomGenerator;

public class Weapon extends Artifact {
    public Weapon() {
        this.className = "Weapon";
        this.imageUrl = "/assets/sword.png";
    }

    public Weapon(int level) {
        this.className = "Weapon";
        this.attack = level * (RandomGenerator.getInstance().nextInt(11) + 10);
        this.defense = 0;
        this.hitPoints = 0;
        this.imageUrl = "/assets/sword.png";
    }
}
