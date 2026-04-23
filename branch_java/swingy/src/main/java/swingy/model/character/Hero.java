package swingy.model.character;

import swingy.model.artifact.Artifact;

import java.util.ArrayList;
import java.util.List;

public abstract class Hero {
    protected String name;
    protected String className;
    protected int level = 1;
    protected int experience = 0;
    protected int attack;
    protected int defense;
    protected int hitPoints;
    protected int crit;
    protected String imageUrl;
    protected String deadImageUrl = "/assets/hero_dead.png";
    protected List<Artifact> artifacts = new ArrayList<>();

    public Hero() {}

    public Hero(Hero hero) {
        this.name = hero.name;
        this.className = hero.className;
        this.level = hero.level;
        this.experience = hero.experience;
        this.attack = hero.attack;
        this.defense = hero.defense;
        this.hitPoints = hero.hitPoints;
        this.crit = hero.crit;
        this.imageUrl = hero.imageUrl;
        this.artifacts.addAll(hero.artifacts);
    }

    public void addExperience(int experience) {
        this.experience += experience;
        int expToLevelUp = (int) (this.level * 1000 + Math.pow((this.level - 1), 2) * 450);
        if (this.experience >= expToLevelUp) {
            levelUp();
        }
    }

    private void levelUp() {
        this.level += 1;
        this.experience = 0;
        this.attack += 10;
        this.defense += 10;
        this.hitPoints += 50;
    }

    public void setArtifacts(List<Artifact> artifacts) {
        this.artifacts.addAll(artifacts);
        for (Artifact artifact : artifacts) {
            this.attack += artifact.getAttack();
            this.defense += artifact.getDefense();
            this.hitPoints += artifact.getHitPoints();
        }
    }

    public void setArtifacts(Artifact artifact) {
        this.artifacts.add(artifact);
        this.attack += artifact.getAttack();
        this.defense += artifact.getDefense();
        this.hitPoints += artifact.getHitPoints();
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getClassName() {
        return this.className;
    }
    public int getLevel() {
        return this.level;
    }
    public int getExperience() {
        return this.experience;
    }
    public int getAttack() {
        return this.attack;
    }
    public int getDefense() {
        return this.defense;
    }
    public int getHitPoints() {
        return this.hitPoints;
    }
    public int getCrit() {
        return this.crit;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public String getDeadImageUrl() {
        return this.deadImageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public List<Artifact> getArtifacts() {
        return this.artifacts;
    }
}
