package src.flyables.aircrafts;

import src.utils.Coordinates;

public class Helicopter extends Aircraft {
    public Helicopter(long p_id, String p_name, Coordinates p_coordinate) {
        super(p_id, p_name, p_coordinate);
    }

    @Override
    public void updateConditions() {
        super.updateConditions();
    }
}