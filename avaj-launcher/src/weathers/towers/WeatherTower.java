package src.weathers.towers;

import src.utils.Coordinates;
import src.weathers.WeatherProvider; 
import src.flyables.Flyable;

public class WeatherTower extends Tower {

    public String getWeather(Coordinates p_coordinates) {
        return WeatherProvider.getCurrentWeather(p_coordinates);
    }

    public void changeWeather() {
        this.conditionChanged();
    }
}