package src.flyables.aircrafts;

import src.flyables.Flyable;
import src.coordinates.Coordinates;
import src.exceptions.BadFlyableException;

public class AircraftFactory {
    private static AircraftFactory instance;
    private long flyableCount;

    private AircraftFactory() {
        this.flyableCount = 0;
    }

    public static AircraftFactory getInstance() {
        if (instance == null) {
            instance = new AircraftFactory();
        }
        return instance;
    }

    public Flyable newAircraft(String p_type, String p_name, Coordinates p_coordinates) throws BadFlyableException {
        if (p_type.toLowerCase().equals("helicopter")) {
            return new Helicopter(++this.flyableCount, p_name, p_coordinates);
        } else if (p_type.toLowerCase().equals("jetplane") || p_type.toLowerCase().equals("jet plane")) {
            return new JetPlane(++this.flyableCount, p_name, p_coordinates);
        } else if (p_type.toLowerCase().equals("baloon")) {
            return new Balloon(++this.flyableCount , p_name, p_coordinates);
        } else {
            throw new BadFlyableException("Flyable doesn't exist!");
        }
    }
}