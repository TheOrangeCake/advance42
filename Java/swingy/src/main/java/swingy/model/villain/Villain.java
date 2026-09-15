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

    public abstract Villain deepCopy();

    protected void copyFields(Villain target) {
        target.level = this.level;
        target.className = this.className;
        target.attack = this.attack;
        target.defense = this.defense;
        target.hitPoints = this.hitPoints;
        target.crit = this.crit;
        target.experience = this.experience;
        target.imageUrl = this.imageUrl;
    }

    public void takeHit(int damage) {
        this.hitPoints -= damage;
    }

    public int getLevel() {
        return this.level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public String getClassName() {
        return this.className;
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
    public int getCrit() {
        return this.crit;
    }
    public void setCrit(int crit) {
        this.crit = crit;
    }
    public int getExperience() {
        return this.experience;
    }
    public void setExperience(int experience) {
        this.experience = experience;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
