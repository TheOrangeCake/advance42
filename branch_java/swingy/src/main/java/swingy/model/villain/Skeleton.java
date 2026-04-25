package swingy.model.villain;

import swingy.utils.RandomGenerator;

public class Skeleton extends Villain {
    public Skeleton(int level) {
        float modifier = Math.clamp(((float) (RandomGenerator.getInstance().nextInt(100) + 1) / 100), 0.5f, 1.0f);
        this.level = level;
        this.className = "Skeleton";
        double scale = Math.pow(level, 2.6);
        this.attack = (int)((10 + level) * scale * modifier);
        this.defense = (int)((6 + level) * scale * modifier);
        this.hitPoints = (int)((60 + level) * scale * modifier);
        this.crit = 10;
        this.experience = (int)(400 * level * modifier);
        this.imageUrl = "/assets/skeleton.png";
    }
}
