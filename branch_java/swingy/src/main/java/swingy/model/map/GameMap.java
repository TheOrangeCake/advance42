package swingy.model.map;

import swingy.model.artifact.Artifact;
import swingy.model.artifact.ArtifactFactory;
import swingy.model.character.Hero;
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
    private Map<String, Villain> villains = new HashMap<>();
    private Artifact droppedArtifact = null;

    public GameMap() {

    }

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
    // Sorry this is ugly, but whatever
    public int isWin(Hero hero) {
        Villain villain = getVillainAtHeroPosition();
        int heroInitialHp = hero.getHitPoints();
        while (true) {
            int hAttack = hero.getAttack();
            boolean isHeroCrit = (RandomGenerator.getInstance().nextInt(100) + 1) <= hero.getCrit();
            if (isHeroCrit) {
                hAttack = (int)(hAttack * 1.5);
            }
            int heroDmg = Math.max(hAttack - villain.getDefense(), 0);
            if (heroDmg == 0) {
                heroDmg = 1;
            }
            villain.takeHit(heroDmg);
            if (villain.getHitPoints() <= 0) {
                removeVillainAtHeroPosition();
                hero.heal(heroInitialHp / 4);
                if (hero.getHitPoints() > heroInitialHp) {
                    hero.setHitPoints(heroInitialHp);
                }
                hero.addExperience(villain.getExperience());
                this.droppedArtifact = ArtifactFactory.generateArtifact(villain.getLevel());
                return villain.getLevel();
            }

            int vAttack = villain.getAttack();
            boolean isVillainCrit = (RandomGenerator.getInstance().nextInt(100) + 1) <= villain.getCrit();
            if (isVillainCrit) {
                vAttack = (int)(vAttack * 1.5);
            }
            int villainDmg = Math.max(vAttack - hero.getDefense(), 0);
            if (villainDmg == 0) {
                villainDmg = 1;
            }
            hero.takeHit(villainDmg);
            if (hero.getHitPoints() <= 0) {
                return -1;
            }
        }
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

    public void equipDroppedArtifact(Hero hero) {
        hero.setArtifact(this.droppedArtifact);
        clearDroppedArtifact();
    }

    public void clearDroppedArtifact() {
        this.droppedArtifact = null;
    }

    public int getLevel() {
        return this.level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public Map<String, Villain> getVillains() {
        return this.villains;
    }
    public void setVillains(Map<String, Villain> villains) {
        this.villains = villains;
    }
    public int getSize() {
        return this.size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public int[] getHeroPosition() {
        return this.heroPosition;
    }
    public void setHeroPosition(int x, int y) {
        this.heroPosition[0] = x;
        this.heroPosition[1] = y;
    }
    public int[] getPrevPosition() {
        return prevPosition;
    }
    public void setPrevPosition(int x, int y) {
        this.prevPosition[0] = x;
        this.prevPosition[1] = y;
    }
    public int getNbVillain() {
        return nbVillain;
    }
    public void setNbVillain(int nbVillain) {
        this.nbVillain = nbVillain;
    }
    public Artifact getDroppedArtifact() {
        return this.droppedArtifact;
    }
    public void setDroppedArtifact(Artifact droppedArtifact) {
        this.droppedArtifact = droppedArtifact;
    }
}
