package src.flyables.aircrafts;

import src.utils.Coordinates;
import src.flyables.Flyable;
import src.exceptions.BadWeatherException;
import src.exceptions.BadProgrammerException;

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
    public void updateConditions() throws BadWeatherException, BadProgrammerException {
        System.out.println("Do not use this");
        throw new BadProgrammerException();
    }
    

    @Override
    public String getFlyableModel() {
        return (this.getType() +
                "#" +
                this.getName() +
                "(" +
                this.getId() +
                ")");
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Coordinates getCoordinate() {
        return this.coordinate;
    }
}