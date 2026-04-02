package src.flyables.aircrafts;

import src.coordinates.Coordinates;

public class Balloon extends Aircraft {
    public Balloon(long p_id, String p_name, Coordinates p_coordinate) {
        super(p_id, p_name, p_coordinate);
    }

    @Override
    public void updateConditions() {
        super.updateConditions();
    }
}