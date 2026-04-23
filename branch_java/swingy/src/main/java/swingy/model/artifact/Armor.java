package swingy.model.artifact;

import swingy.utils.RandomGenerator;

public class Armor extends Artifact {
    public Armor(int level) {
        this.attack = 0;
        this.defense = level * RandomGenerator.getInstance().nextInt(101 - 10) + 10;
        this.hitPoints = 0;
        this.imageUrl = "/assets/shield.png";
    }
}
