package src.flyables.aircrafts;

import src.utils.Coordinates;
import src.exceptions.BadWeatherException;

public class JetPlane extends Aircraft {
    public JetPlane(long p_id, String p_name, Coordinates p_coordinate) {
        super(p_id, p_name, p_coordinate);
    }

    @Override
    public void updateConditions() throws BadWeatherException {
        String weather = this.weatherTower.getWeather(this.coordinate);
        if (weather.equals("SUN")) {
            this.coordinate.increaseLongitude(2);
            this.coordinate.increaseHeight(4);
        } else if (weather.equals("RAIN")) {
            this.coordinate.decreaseHeight(5);
        } else if (weather.equals("FOG")) {
            this.coordinate.decreaseHeight(3);
        } else if (weather.equals("SNOW")) {
            this.coordinate.decreaseHeight(15);
        } else {
            throw new BadWeatherException();
        }
    }
}