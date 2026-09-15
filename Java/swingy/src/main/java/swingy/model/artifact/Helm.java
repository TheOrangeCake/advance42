package swingy.model.artifact;

import swingy.utils.RandomGenerator;

public class Helm extends Artifact {
    public Helm() {
        this.className = "Helm";
        this.imageUrl = "/assets/helm.png";
    }

    @Override
    public Artifact deepCopy() {
        Helm copy = new Helm();
        copyFields(copy);
        return copy;
    }

    public Helm(int level) {
        this.className = "Helm";
        this.attack = 0;
        this.defense = 0;
        this.hitPoints = level * (RandomGenerator.getInstance().nextInt(51) + 50);
        this.imageUrl = "/assets/helm.png";
    }
}
