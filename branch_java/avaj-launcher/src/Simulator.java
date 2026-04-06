import src.flyables.aircrafts.AircraftFactory;
import src.flyables.Flyable;
import src.weathers.WeatherProvider;
import src.weathers.towers.WeatherTower;
import src.utils.FileReader;
import src.utils.Scenario;
import src.utils.FileWriter;
import src.exceptions.BadFileException;
import src.exceptions.BadProgrammerException;

public class Simulator {
    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("Error: require 1 scenario file");
            System.exit(1);
        }

        AircraftFactory.getInstance();
        Scenario.getInstance();
        try {
            FileReader reader = new FileReader(args[0]);
            reader.createScenario();
        } catch (BadFileException | BadProgrammerException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        WeatherProvider.getInstance();
        WeatherTower weatherTower = new WeatherTower();
        for (Flyable flyable : Scenario.getFlyables()) {
            weatherTower.register(flyable);
            flyable.registerTower(weatherTower);
        }
        FileWriter.writeLine("-------------------------------------");
        for (int i = 0; i < Scenario.getNumberOfTurns(); i++) {
            weatherTower.changeWeather();
            if (weatherTower.getObservers().isEmpty()) {
                break;
            }
        }
    }
}
