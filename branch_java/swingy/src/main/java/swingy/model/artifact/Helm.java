package swingy.model.artifact;

import swingy.utils.RandomGenerator;

public class Helm extends Artifact {
    public Helm() {
        this.attack = 0;
        this.defense = 0;
        this.hitPoints = RandomGenerator.getInstance().nextInt(151 - 50) + 50;
        this.imageUrl = "/assets/helm.png";
    }
}

