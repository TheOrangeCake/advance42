package src.weathers;

import src.coordinates.Coordinates;

public class WeatherProvider {
    private static WeatherProvider instance;
    private String[] weather = {"RAIN", "FOG", "SUN", "SNOW"};

    private WeatherProvider() {

    }

    public static WeatherProvider getInstance() {
        if (instance == null) {
            instance = new WeatherProvider();
        }
        return instance;
    }

    public String getCurrentWeather(Coordinates p_coordinate) {
        return null;
    }
}