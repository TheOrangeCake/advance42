package src.weathers;

import src.utils.Coordinates;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class WeatherProvider {
    private static WeatherProvider instance;
    private static String[] weather = {"RAIN", "FOG", "SUN", "SNOW"};
    private static Random randomGenerator;
    private static Map<Integer, String> weatherMap;

    private WeatherProvider() {
        randomGenerator = new Random();
        weatherMap = new HashMap<>();
    }

    public static WeatherProvider getInstance() {
        if (instance == null) {
            instance = new WeatherProvider();
        }
        return instance;
    }

    public static String getCurrentWeather(Coordinates p_coordinate) {
        Integer coordinate = p_coordinate.hashCode();
        if (weatherMap.containsKey(coordinate)) {
            return weatherMap.get(coordinate);
        }
        String currentWeather = weather[randomGenerator.nextInt(weather.length)];
        weatherMap.put(coordinate, currentWeather);
        return currentWeather;
    }
}