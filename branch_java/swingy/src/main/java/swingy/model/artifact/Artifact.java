package swingy.model.artifact;

public abstract class Artifact {
    protected String name;
    protected int attack;
    protected int defense;
    protected int hitPoints;
    protected String imageUrl;

    public String getName() {
        return this.name;
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
    public String getImageUrl() {
        return this.imageUrl;
    }
}
