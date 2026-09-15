package swingy.model.state;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "ARTIFACT")
public class ArtifactState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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

    @Column(name = "image_url")
    private String imageUrl;

    public int getId() { return id; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public int getAttack() { return attack; }
    public void setAttack(int attack) { this.attack = attack; }

    public int getDefense() { return defense; }
    public void setDefense(int defense) { this.defense = defense; }

    public int getHitPoints() { return hitPoints; }
    public void setHitPoints(int hitPoints) { this.hitPoints = hitPoints; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
