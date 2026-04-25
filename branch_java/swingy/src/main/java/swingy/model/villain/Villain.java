package swingy.model.villain;

public abstract class Villain {
    protected int level;
    protected String className;
    protected int attack;
    protected int defense;
    protected int hitPoints;
    protected int crit;
    protected int experience;
    protected String imageUrl;

    public void takeHit(int damage) {
        this.hitPoints -= damage;
    }

    public int getLevel() {
        return this.level;
    }
    public String getClassName() {
        return this.className;
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
    public int getExperience() {
        return this.experience;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
}
