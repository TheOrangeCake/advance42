package src.flyables.aircrafts;

import src.utils.Coordinates;
import src.utils.FileWriter;
import src.exceptions.BadWeatherException;
import src.exceptions.BadProgrammerException;

public class Balloon extends Aircraft {
    public Balloon(long p_id, String p_name, Coordinates p_coordinate) {
        super(p_id, p_name, p_coordinate);
    }

    @Override
    public void updateConditions() throws BadWeatherException, BadProgrammerException {
        String weather = this.weatherTower.getWeather(this.coordinate);
        if (weather.equals("SUN")) {
            this.coordinate.changeLongitude(2);
            this.coordinate.changeHeight(4);
            FileWriter.writeLine(this.getFlyableModel() + ":\t\t" + "SUNNY" + ".\t\t" + this.coordinate.toString());
        } else if (weather.equals("RAIN")) {
            this.coordinate.changeHeight(-5);
            FileWriter.writeLine(this.getFlyableModel() + ":\t\t" + "RAINY" + ".\t\t" + this.coordinate.toString());
        } else if (weather.equals("FOG")) {
            this.coordinate.changeHeight(-3);
            FileWriter.writeLine(this.getFlyableModel() + ":\t\t" + "FOGGY" + ".\t\t" + this.coordinate.toString());
        } else if (weather.equals("SNOW")) {
            this.coordinate.changeHeight(-15);
            FileWriter.writeLine(this.getFlyableModel() + ":\t\t" + "SNOWY" + ".\t\t" + this.coordinate.toString());
        } else {
            throw new BadWeatherException();
        }
        if (this.coordinate.getHeight() <= 0) {
            FileWriter.writeLine(this.getFlyableModel() + " landing.");
            this.weatherTower.unregister(this);
        }
    }
}