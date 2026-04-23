package swingy.model.artifact;

import swingy.utils.RandomGenerator;

public class ArtifactFactory {
    private ArtifactFactory() {}

    public static Artifact generateArtifact(int level) {
        int random = RandomGenerator.getInstance().nextInt(4);
        return switch (random) {
            case 0 -> new Helm(level);
            case 1 -> new Armor(level);
            default -> new Weapon(level);
        };
    }
}