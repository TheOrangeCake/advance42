package swingy.model.artifact;

import swingy.utils.RandomGenerator;

public class Weapon extends Artifact {
    public Weapon(int modifier) {
        this.attack = modifier * RandomGenerator.getInstance().nextInt(101 - 10) + 10;
        this.defense = 0;
        this.hitPoints = 0;
        this.imageUrl = "/assets/sword.png";
    }
}
