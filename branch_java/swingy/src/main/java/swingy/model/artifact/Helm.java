package swingy.model.artifact;

import swingy.utils.RandomGenerator;

public class Helm extends Artifact {
    public Helm(int level) {
        this.attack = 0;
        this.defense = 0;
        this.hitPoints = level * (RandomGenerator.getInstance().nextInt(51) + 50);
        this.imageUrl = "/assets/helm.png";
    }
}
