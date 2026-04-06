package src.flyables;

import src.weathers.towers.WeatherTower;
import src.utils.Coordinates;
import src.exceptions.BadWeatherException;

public abstract class Flyable {
    protected WeatherTower weatherTower;

    public abstract void updateConditions() throws BadWeatherException;
    public void registerTower(WeatherTower p_tower) {
        this.weatherTower = p_tower;
    };

    public abstract String getFlyableModel();
    public abstract long getId();
    public abstract String getType();
    public abstract String getName();
    public abstract Coordinates getCoordinate();
}