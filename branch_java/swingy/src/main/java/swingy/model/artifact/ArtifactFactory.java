package swingy.model.artifact;

import swingy.utils.RandomGenerator;

public enum ArtifactFactory {
    INSTANCE;

    public Artifact getArtifact(int modifier) {
        int random = RandomGenerator.getInstance().nextInt(4);
        return switch (random) {
            case 0 -> new Helm(modifier);
            case 1 -> new Armor(modifier);
            default -> new Weapon(modifier);
        };
    }
}