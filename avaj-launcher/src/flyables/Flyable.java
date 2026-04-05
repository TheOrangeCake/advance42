package src.flyables;

import src.weathers.towers.WeatherTower;
import src.utils.Coordinates;

public abstract class Flyable {
    protected WeatherTower weatherTower;

    public abstract void updateConditions();
    public void registerTower(WeatherTower p_tower) {
        this.weatherTower = p_tower;
    };

    public abstract long getId();
    public abstract String getName();
    public abstract Coordinates getcoordinate();
}