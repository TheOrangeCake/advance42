package src.flyables.aircrafts;

import src.utils.Coordinates;
import src.utils.FileWriter;
import src.exceptions.BadWeatherException;
import src.exceptions.BadProgrammerException;

public class JetPlane extends Aircraft {
    public JetPlane(long p_id, String p_name, Coordinates p_coordinate) {
        super(p_id, p_name, p_coordinate);
    }

    @Override
    public void updateConditions() throws BadWeatherException, BadProgrammerException {
        String weather = this.weatherTower.getWeather(this.coordinate);
        if (weather.equals("SUN")) {
            this.coordinate.changeLatitude(10);
            this.coordinate.changeHeight(2);
            FileWriter.writeLine(this.getFlyableModel() + ":\t\t" + "SUNNY" + ".\t\t" + this.coordinate.toString());
        } else if (weather.equals("RAIN")) {
            this.coordinate.changeLatitude(5);
            FileWriter.writeLine(this.getFlyableModel() + ":\t\t" + "RAINY" + ".\t\t" + this.coordinate.toString());
        } else if (weather.equals("FOG")) {
            this.coordinate.changeLatitude(1);
            FileWriter.writeLine(this.getFlyableModel() + ":\t\t" + "FOGGY" + ".\t\t" + this.coordinate.toString());
        } else if (weather.equals("SNOW")) {
            this.coordinate.changeHeight(-7);
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