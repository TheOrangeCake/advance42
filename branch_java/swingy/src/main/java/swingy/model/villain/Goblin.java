package swingy.model.villain;

import swingy.utils.RandomGenerator;

public class Goblin extends Villain {
    public Goblin(int level) {
        float modifier = Math.clamp(((float) (RandomGenerator.getInstance().nextInt(100) + 1) / 100), 0.5f, 1.0f);
        this.level = level;
        this.className = "Goblin";
        this.attack = (int) (10 * level * modifier);
        this.defense = (int) (5 * level * modifier);
        this.hitPoints = (int) (55 * level * modifier);
        this.crit = 5;
        this.experience = (int) (250 * level * modifier);
        this.imageUrl = "/assets/goblin.png";
    }
}
