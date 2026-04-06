package src.flyables;

import src.weathers.towers.WeatherTower;
import src.utils.Coordinates;
import src.exceptions.BadWeatherException;
import src.exceptions.BadProgrammerException;

public abstract class Flyable {
    protected WeatherTower weatherTower;

    public abstract void updateConditions() throws BadWeatherException, BadProgrammerException;
    
    public void registerTower(WeatherTower p_tower) {
        this.weatherTower = p_tower;
    };

    public abstract String getFlyableModel();
    public abstract long getId();
    public abstract String getType();
    public abstract String getName();
    public abstract Coordinates getCoordinate();
}