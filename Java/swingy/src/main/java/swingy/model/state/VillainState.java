package swingy.model.state;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "VILLAIN")
public class VillainState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "position_key")
    private String positionKey;

    @Column(name = "level")
    @Min(1)
    private int level;

    @NotBlank
    @Column(name = "class_name")
    private String className;

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

    @Column(name = "experience")
    @PositiveOrZero
    private int experience;

    @Column(name = "image_url")
    private String imageUrl;

    public int getId() { return id; }

    public String getPositionKey() { return positionKey; }
    public void setPositionKey(String positionKey) { this.positionKey = positionKey; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public int getAttack() { return attack; }
    public void setAttack(int attack) { this.attack = attack; }

    public int getDefense() { return defense; }
    public void setDefense(int defense) { this.defense = defense; }

    public int getHitPoints() { return hitPoints; }
    public void setHitPoints(int hitPoints) { this.hitPoints = hitPoints; }

    public int getCrit() { return crit; }
    public void setCrit(int crit) { this.crit = crit; }

    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}