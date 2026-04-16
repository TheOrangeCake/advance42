package src.model;

public abstract class Hero {
    protected String name;
    protected int level;
    protected int experience;
    protected int attack;
    protected int defense;
    protected int hitPoints;

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return this.level;
    }
    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return this.experience;
    }
    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getAttack() {
        return this.attack;
    }
    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return this.defense;
    }
    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getHitPoints() {
        return this.hitPoints;
    }
    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }
}
