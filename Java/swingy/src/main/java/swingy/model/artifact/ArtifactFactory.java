package swingy.model.artifact;

import swingy.utils.RandomGenerator;

public class ArtifactFactory {
    private ArtifactFactory() {}

    public static Artifact generateArtifact(int level) {
        int random = RandomGenerator.getInstance().nextInt(6);
        return switch (random) {
            case 0 -> new Helm(level);
            case 1 -> new Armor(level);
            case 2, 3 -> new Weapon(level);
            default -> null;
        };
    }

    public static Artifact generateArtifact(String className) {
        return switch (className) {
            case "Armor" -> new Armor();
            case "Helm" -> new Helm();
            case "Weapon" -> new Weapon();
            default -> null;
        };
    }
}
