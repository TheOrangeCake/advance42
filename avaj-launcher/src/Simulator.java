import src.flyables.aircrafts.AircraftFactory;
import src.weathers.WeatherProvider;
import src.utils.FileReader;
import src.utils.Scenario;
import src.exceptions.BadFileException;

public class Simulator {
    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("Error: require 1 scenario file");
            return;
        }

        AircraftFactory aircraftFactory = AircraftFactory.getInstance();
        WeatherProvider weatherprovider = WeatherProvider.getInstance();

        try {
            FileReader reader = new FileReader(args[0]);
            Scenario scenario = reader.createScenario();
            if (scenario == null) {
                return;
            }

        } catch (BadFileException e) {
            System.out.println(e.getMessage());
            return;
        }
    }
}
