package swingy.model.state.mapper;

import swingy.model.map.GameMap;
import swingy.model.state.GameMapState;
import swingy.model.state.HeroState;
import swingy.model.state.VillainState;
import swingy.model.villain.Villain;

import java.util.List;

public class GameMapMapper {
    public static GameMapState mapToState(GameMap gameMap, HeroState heroState) {
        GameMapState state = new GameMapState();
        state.setHero(heroState);
        state.setLevel(gameMap.getLevel());
        state.setSize(gameMap.getSize());
        state.setCurrentX(gameMap.getHeroPosition()[0]);
        state.setCurrentY(gameMap.getHeroPosition()[1]);
        state.setPreviousX(gameMap.getPrevPosition()[0]);
        state.setPreviousY(gameMap.getPrevPosition()[1]);
        state.setNbVillain(gameMap.getNbVillain());
        List<VillainState> villainStates = gameMap.getVillains().entrySet().stream()
                .map(entry -> VillainMapper.mapToState(entry.getValue(), entry.getKey()))
                .toList();
        state.setVillains(villainStates);
        if (gameMap.getDroppedArtifact() != null) {
            state.setDroppedArtifact(ArtifactMapper.mapToState(gameMap.getDroppedArtifact()));
        }
        return state;
    }

    public static GameMap mapToObject(GameMapState state) {
        GameMap gameMap = new GameMap();
        gameMap.setLevel(state.getLevel());
        gameMap.setSize(state.getSize());
        gameMap.setHeroPosition(state.getCurrentX(), state.getCurrentY());
        gameMap.setPrevPosition(state.getPreviousX(), state.getPreviousY());
        gameMap.setNbVillain(state.getNbVillain());
        for (VillainState villainState : state.getVillains()) {
            Villain villain = VillainMapper.mapToObject(villainState);
            if (villain != null) {
                gameMap.getVillains().put(villainState.getPositionKey(), villain);
            }
        }
        if (state.getDroppedArtifact() != null) {
            gameMap.setDroppedArtifact(ArtifactMapper.mapToObject(state.getDroppedArtifact()));
        }
        return gameMap;
    }
}