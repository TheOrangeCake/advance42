package swingy.model.state.mapper;

import swingy.model.artifact.Artifact;
import swingy.model.artifact.ArtifactFactory;
import swingy.model.state.ArtifactState;
import swingy.utils.Colors;

public class ArtifactMapper {
    public static ArtifactState mapToState(Artifact artifact) {
        if (artifact == null) {
            return null;
        }
        ArtifactState state = new ArtifactState();
        state.setClassName(artifact.getClassName());
        state.setAttack(artifact.getAttack());
        state.setDefense(artifact.getDefense());
        state.setHitPoints(artifact.getHitPoints());
        state.setImageUrl(artifact.getImageUrl());
        return state;
    }

    public static Artifact mapToObject(ArtifactState state) {
        if (state == null) {
            return null;
        }
        Artifact artifact = ArtifactFactory.generateArtifact(state.getClassName());
        if (artifact == null) {
            System.err.println(Colors.RED + "Error" + Colors.RESET + ": Fail to map artifact, ignored");
            return null;
        }
        artifact.setAttack(state.getAttack());
        artifact.setDefense(state.getDefense());
        artifact.setHitPoints(state.getHitPoints());
        return artifact;
    }
}
