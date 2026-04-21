package swingy.model.character;

public class Defender extends Hero {
    public Defender(String name) {
        this.name = name;
        this.className = "Defender";
        this.level = 0;
        this.experience = 0;
        this.attack = 10;
        this.defense = 20;
        this.hitPoints = 200;
        this.image = "/assets/defender.png";
    }
}
