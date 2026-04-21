package swingy.model.character;

import swingy.model.PlayerName;

public class Fighter extends Hero {
    public Fighter(PlayerName name) {
        this.name = name;
        this.className = "Fighter";
        this.level = 0;
        this.experience = 0;
        this.attack = 30;
        this.defense = 10;
        this.hitPoints = 100;
        this.image = "/assets/fighter.png";
    }
}
