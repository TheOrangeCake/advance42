package swingy.model.villain;

public abstract class Villain {
    protected int level;
    protected String className;
    protected int attack;
    protected int defense;
    protected int hitPoints;
    protected int crit;
    protected String imageUrl;

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
    public String getImageUrl() {
        return this.imageUrl;
    }
}
