import src.flyables.aircrafts.AircraftFactory;
import src.flyables.Flyable;
import src.weathers.WeatherProvider;
import src.weathers.towers.WeatherTower;
import src.utils.FileReader;
import src.utils.Scenario;
import src.exceptions.BadFileException;

import src.utils.Coordinates;

public class Simulator {
    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("Error: require 1 scenario file");
            System.exit(1);
        }

        AircraftFactory.getInstance();
        Scenario scenario = null;
        try {
            FileReader reader = new FileReader(args[0]);
            scenario = reader.createScenario();
            if (scenario == null) {
                System.exit(1);
            }
        } catch (BadFileException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        WeatherProvider.getInstance();
        WeatherTower weatherTower = new WeatherTower();
        for (Flyable flyable : scenario.getFlyables()) {
            weatherTower.register(flyable);
            flyable.registerTower(weatherTower);
        }
        
        for (int i = 0; i < scenario.getNumberOfTurns(); i++) {
            weatherTower.changeWeather();
        }
    }
}
