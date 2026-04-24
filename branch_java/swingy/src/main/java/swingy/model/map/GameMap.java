package swingy.model.map;

import swingy.model.villain.Villain;
import swingy.model.villain.VillainFactory;
import swingy.utils.RandomGenerator;

import java.util.HashMap;
import java.util.Map;

public class GameMap {
    private int level;
    private int size;
    private final int[] heroPosition = {0, 0};
    private final int[] prevPosition = {0, 0};
    private int nbVillain;
    private final Map<String, Villain> villains = new HashMap<>();

    public GameMap(int level) {
        if (level <= 0) {
            level = 1;
        }
        this.level = level;
        calculateSize();
        this.heroPosition[0] = this.size / 2 + 1;
        this.heroPosition[1] = this.size / 2 + 1;
        this.prevPosition[0] = this.size / 2 + 1;
        this.prevPosition[1] = this.size / 2 + 1;
        calculateNbVillain();
        generateVillains();
    }

    public void moveHero(int horizontal, int vertical) {
        int x = Math.clamp(horizontal, -1, 1);
        int y = Math.clamp(vertical, -1, 1);
        this.prevPosition[0] = this.heroPosition[0];
        this.prevPosition[1] = this.heroPosition[1];
        this.heroPosition[0] += x;
        this.heroPosition[1] += y;
        if (this.heroPosition[0] > this.size
                || this.heroPosition[0] < 1
                || this.heroPosition[1] > this.size
                || this.heroPosition[1] < 1) {
            this.levelUp();
        }
    }

    public Villain getVillainAtHeroPosition() {
        return villains.get(heroPosition[0] + "," + heroPosition[1]);
    }

    public void removeVillainAtHeroPosition() {
        villains.remove(heroPosition[0] + "," + heroPosition[1]);
    }

    public boolean isEncounter() {
        return villains.containsKey(heroPosition[0] + "," + heroPosition[1]);
    }

    // return level of villain if won, -1 if lost
    // Sorry this is ugly, but it is what it is
    public int isWin() {
        System.out.println("Combat!!");
        Villain villain = getVillainAtHeroPosition();
        removeVillainAtHeroPosition();
        return villain.getLevel();
    }

    public void resetPrevPosition() {
        this.heroPosition[0] = this.prevPosition[0];
        this.heroPosition[1] = this.prevPosition[1];
    }

    private void levelUp() {
        this.level += 1;
        calculateSize();
        this.heroPosition[0] = this.size / 2 + 1;
        this.heroPosition[1] = this.size / 2 + 1;
        this.prevPosition[0] = this.size / 2 + 1;
        this.prevPosition[1] = this.size / 2 + 1;
        calculateNbVillain();
        this.villains.clear();
        generateVillains();
    }

    private void calculateSize() {
        this.size = (this.level - 1) * 5 + 10 - (this.level % 2);
    }

    private void calculateNbVillain() {
        int halfSize = this.size / 2;
        this.nbVillain = (int) Math.pow(halfSize, 2);
    }

    private void generateVillains() {
        int placed = 0;
        while (placed < this.nbVillain) {
            int x = RandomGenerator.getInstance().nextInt(this.size) + 1;
            int y = RandomGenerator.getInstance().nextInt(this.size) + 1;
            String key = x + "," + y;
            if ((x == this.size / 2 + 1 && y == this.size / 2 + 1)
                    || this.villains.containsKey(key)) {
                continue;
            }
            this.villains.put(key, VillainFactory.generateVillain(this.level));
            placed++;
        }
    }

    public int getLevel() {
        return this.level;
    }
    public Map<String, Villain> getVillains() {
        return this.villains;
    }
    public int getSize() {
        return this.size;
    }
    public int[] getHeroPosition() {
        return heroPosition;
    }
}
