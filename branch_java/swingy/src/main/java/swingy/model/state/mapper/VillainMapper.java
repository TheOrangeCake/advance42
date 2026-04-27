package swingy.model.state.mapper;

import swingy.model.state.VillainState;
import swingy.model.villain.Goblin;
import swingy.model.villain.Skeleton;
import swingy.model.villain.Villain;
import swingy.utils.Colors;

public class VillainMapper {
    public static VillainState mapToState(Villain villain, String positionKey) {
        VillainState state = new VillainState();
        state.setClassName(villain.getClassName());
        state.setPositionKey(positionKey);
        state.setLevel(villain.getLevel());
        state.setAttack(villain.getAttack());
        state.setDefense(villain.getDefense());
        state.setHitPoints(villain.getHitPoints());
        state.setCrit(villain.getCrit());
        state.setExperience(villain.getExperience());
        state.setImageUrl(villain.getImageUrl());
        return state;
    }

    public static Villain mapToObject(VillainState state) {
        Villain villain;
        switch (state.getClassName()) {
            case "Skeleton":
                villain = new Skeleton();
                break;
            case "Goblin":
                villain = new Goblin();
                break;
            default:
                System.err.println(Colors.RED + "Error" + Colors.RESET + ": Fail to load villain, ignored");
                return null;
        }
        villain.setLevel(state.getLevel());
        villain.setAttack(state.getAttack());
        villain.setDefense(state.getDefense());
        villain.setHitPoints(state.getHitPoints());
        villain.setCrit(state.getCrit());
        villain.setExperience(state.getExperience());
        villain.setImageUrl(state.getImageUrl());
        return villain;
    }
}
