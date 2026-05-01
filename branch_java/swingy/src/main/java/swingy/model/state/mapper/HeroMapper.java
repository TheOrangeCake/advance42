package swingy.model.state.mapper;

import swingy.model.artifact.Artifact;
import swingy.model.character.Defender;
import swingy.model.character.Fighter;
import swingy.model.character.Hero;
import swingy.model.state.ArtifactState;
import swingy.model.state.HeroState;
import swingy.utils.Colors;

import java.util.List;

public class HeroMapper {
    public static HeroState mapToState(Hero hero, int slot) {
        if (hero == null || slot < 1 || slot > 3) {
            return null;
        }
        HeroState state = new HeroState();
        state.setSlot(slot);

        state.setName(hero.getName());
        state.setClassName(hero.getClassName());
        state.setLevel(hero.getLevel());
        state.setExperience(hero.getExperience());
        state.setAttack(hero.getAttack());
        state.setDefense(hero.getDefense());
        state.setHitPoints(hero.getHitPoints());
        state.setCrit(hero.getCrit());
        state.setImageUrl(hero.getImageUrl());
        state.setDeadImageUrl(hero.getDeadImageUrl());
        List<ArtifactState> artifactStates = hero.getArtifacts().stream()
                .map(ArtifactMapper::mapToState).toList();
        state.setArtifacts(artifactStates);
        return state;
    }

    public static Hero mapToObject(HeroState state) {
        if (state == null) {
            return null;
        }
        Hero hero;
        switch (state.getClassName()) {
            case "Defender":
                hero = new Defender();
                break;
            case "Fighter":
                hero = new Fighter();
                break;
            default:
                System.err.println(Colors.RED + "Error" + Colors.RESET + ": Fail to load hero");
                return null;
        }
        hero.setName(state.getName());
        hero.setLevel(state.getLevel());
        hero.setExperience(state.getExperience());
        hero.setAttack(state.getAttack());
        hero.setDefense(state.getDefense());
        hero.setHitPoints(state.getHitPoints());
        hero.setCrit(state.getCrit());
        hero.setDeadImageUrl(state.getDeadImageUrl());
        List<Artifact> artifacts = state.getArtifacts().stream()
                        .map(ArtifactMapper::mapToObject)
                        .toList();
        hero.setArtifacts(artifacts);
        return hero;
    }
}
