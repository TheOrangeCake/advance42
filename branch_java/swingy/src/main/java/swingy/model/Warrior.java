package swingy.model;

public class Warrior extends Hero {
    public Warrior(String name) {
        this.name = name;
        this.level = 0;
        this.experience = 0;
        this.attack = 10;
        this.defense = 10;
        this.hitPoints = 100;
    }
}
