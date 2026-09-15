package swingy.model.villain;

import swingy.utils.RandomGenerator;

public class VillainFactory {
    private VillainFactory() {}

    public static Villain generateVillain(int level) {
        int random = RandomGenerator.getInstance().nextInt(2);
        int generateLevel = Math.max(1, RandomGenerator.getInstance().nextInt(3) + level - 1);
        return switch (random) {
            case 0 -> new Goblin(generateLevel);
            default -> new Skeleton(generateLevel);
        };
    }
}
