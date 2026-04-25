package swingy.model.villain;

import swingy.utils.RandomGenerator;

public class Goblin extends Villain {
    public Goblin(int level) {
        float modifier = Math.clamp(((float) (RandomGenerator.getInstance().nextInt(100) + 1) / 100), 0.5f, 1.0f);
        this.level = level;
        this.className = "Goblin";
        double scale = Math.pow(level, 2.6);
        this.attack = (int)((6 + level) * scale * modifier);
        this.defense = (int)((3 + level) * scale * modifier);
        this.hitPoints = (int)((35 + level) * scale * modifier);
        this.crit = 5;
        this.experience = (int) (250 * level * modifier);
        this.imageUrl = "/assets/goblin.png";
    }
}
