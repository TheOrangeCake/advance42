package swingy.model.artifact;

import swingy.utils.RandomGenerator;

public class Armor extends Artifact {
    public Armor() {
        this.className = "Armor";
        this.imageUrl = "/assets/shield.png";
    }

    @Override
    public Artifact deepCopy() {
        Armor copy = new Armor();
        copyFields(copy);
        return copy;
    }

    public Armor(int level) {
        this.className = "Armor";
        this.attack = 0;
        this.defense = level * (RandomGenerator.getInstance().nextInt(11) + 5);
        this.hitPoints = 0;
        this.imageUrl = "/assets/shield.png";
    }
}
