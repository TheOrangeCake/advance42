package src.flyables.aircrafts;

import src.utils.Coordinates;
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
    
    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Coordinates getcoordinate() {
        return this.coordinate;
    }
}