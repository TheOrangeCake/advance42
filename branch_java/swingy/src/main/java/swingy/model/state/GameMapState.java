package swingy.model.state;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GAME_MAP")
public class GameMapState {
    @Id
    private int slot;

    @OneToOne
    @MapsId
    @JoinColumn(name = "slot")
    private HeroState hero;

    @Column(name = "level")
    @Min(1)
    private int level;

    @Column(name = "size")
    @PositiveOrZero
    private int size;

    @Column(name = "current_x")
    @PositiveOrZero
    private int currentX;

    @Column(name = "current_y")
    @PositiveOrZero
    private int currentY;

    @Column(name = "prev_x")
    @PositiveOrZero
    private int previousX;

    @Column(name = "prev_y")
    @PositiveOrZero
    private int previousY;

    @Column(name = "number_villain")
    @PositiveOrZero
    private int nbVillain;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "map_slot")
    private List<VillainState> villains = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "artifact_slot")
    private ArtifactState droppedArtifact;

    public int getSlot() { return slot; }
    public void setSlot(int slot) { this.slot = slot; }

    public HeroState getHero() { return hero; }
    public void setHero(HeroState hero) { this.hero = hero; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public int getCurrentX() { return currentX; }
    public void setCurrentX(int currentX) { this.currentX = currentX; }

    public int getCurrentY() { return currentY; }
    public void setCurrentY(int currentY) { this.currentY = currentY; }

    public int getPreviousX() { return previousX; }
    public void setPreviousX(int previousX) { this.previousX = previousX; }

    public int getPreviousY() { return previousY; }
    public void setPreviousY(int previousY) { this.previousY = previousY; }

    public int getNbVillain() { return nbVillain; }
    public void setNbVillain(int nbVillain) { this.nbVillain = nbVillain; }

    public List<VillainState> getVillains() { return villains; }
    public void setVillains(List<VillainState> villains) { this.villains = villains; }

    public ArtifactState getDroppedArtifact() { return droppedArtifact; }
    public void setDroppedArtifact(ArtifactState droppedArtifact) { this.droppedArtifact = droppedArtifact; }

}