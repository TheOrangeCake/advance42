package swingy.model.artifact;

import swingy.utils.RandomGenerator;

public enum ArtifactFactory {
    INSTANCE;

    public Artifact getArtifact() {
        int random = RandomGenerator.getInstance().nextInt(4);
        return switch (random) {
            case 0 -> new Helm();
            case 1 -> new Armor();
            default -> new Weapon();
        };
    }
}