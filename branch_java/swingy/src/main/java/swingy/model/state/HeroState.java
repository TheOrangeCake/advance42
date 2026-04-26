package swingy.model.state;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "HERO")
public class HeroState {
    @Id
    @Column(name = "slot")
    @Min(1)
    @Max(3)
    private int slot;

    @OneToOne(mappedBy = "hero", cascade = CascadeType.ALL)
    private GameMapState gameMap;

    @NotBlank
    @Column(name = "name")
    private String name;

    @NotBlank
    @Column(name = "class_name")
    private String className;

    @Column(name = "level")
    @Min(1)
    private int level;

    @Column(name = "experience")
    @PositiveOrZero
    private int experience;

    @Column(name = "attack")
    @PositiveOrZero
    private int attack;

    @Column(name = "defense")
    @PositiveOrZero
    private int defense;

    @Column(name = "hit_points")
    @PositiveOrZero
    private int hitPoints;

    @Column(name = "crit")
    @PositiveOrZero
    private int crit;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "dead_img_url")
    private String deadImageUrl = "/assets/hero_dead.png";

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "hero_slot")
    private List<ArtifactState> artifacts = new ArrayList<>();

    public int getSlot() { return slot; }
    public void setSlot(int slot) { this.slot = slot; }

    public GameMapState getGameMap() { return gameMap; }
    public void setGameMap(GameMapState gameMap) { this.gameMap = gameMap; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }

    public int getAttack() { return attack; }
    public void setAttack(int attack) { this.attack = attack; }

    public int getDefense() { return defense; }
    public void setDefense(int defense) { this.defense = defense; }

    public int getHitPoints() { return hitPoints; }
    public void setHitPoints(int hitPoints) { this.hitPoints = hitPoints; }

    public int getCrit() { return crit; }
    public void setCrit(int crit) { this.crit = crit; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDeadImageUrl() { return deadImageUrl; }
    public void setDeadImageUrl(String deadImageUrl) { this.deadImageUrl = deadImageUrl; }

    public List<ArtifactState> getArtifacts() { return artifacts; }
    public void setArtifacts(List<ArtifactState> artifacts) { this.artifacts = artifacts; }
}