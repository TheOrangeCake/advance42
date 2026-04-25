package swingy.model.artifact;

import swingy.utils.RandomGenerator;

public class Weapon extends Artifact {
    public Weapon(int level) {
        this.attack = level * (RandomGenerator.getInstance().nextInt(11) + 10);
        this.defense = 0;
        this.hitPoints = 0;
        this.imageUrl = "/assets/sword.png";
    }
}
