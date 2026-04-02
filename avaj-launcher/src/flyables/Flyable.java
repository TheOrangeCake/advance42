package src.flyables;

import src.weathers.towers.WeatherTower;

public abstract class Flyable {
    protected WeatherTower weatherTower;

    public abstract void updateConditions();
    public void registerTower(WeatherTower p_tower) {
        this.weatherTower = p_tower;
    }; 
}