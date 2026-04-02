package src.flyables.aircrafts;

import src.coordinates.Coordinates;
import src.flyables.Flyable;

public class Aircraft extends Flyable {
    protected long id;
    protected String name;
    protected Coordinates coordinate;

    protected Aircraft(long p_id, String p_name, Coordinates p_coordinate) {
        this.id = p_id;
        this.name = p_name;
        this.coordinate = p_coordinate;
    }

    @Override
    public void updateConditions() {
        
    }
}