package src.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Scanner;
import src.exceptions.BadFileException;
import src.exceptions.BadFlyableException;
import src.flyables.aircrafts.AircraftFactory;
import src.flyables.Flyable;

public class FileReader {
    private Scanner scanner;
    private Scenario scenario;

    public FileReader(String fileName) throws BadFileException {
        Path path = Paths.get(fileName);
        try {
            this.scanner = new Scanner(path);
            this.scenario = new Scenario();
        } catch(IOException e) {
            throw new BadFileException(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public Scenario createScenario() throws BadFileException {
        if (this.scanner == null) {
            System.err.println("What are you doing?! You closed the scanner already!");
            return null;
        }
        int currentLine = 1;
        parseNumberOfTurns(currentLine++);
        parseFlyables(currentLine);
        this.scanner.close();
        if (this.scenario.getFlyables().isEmpty()) {
            throw new BadFileException("No flyables");
        }
        return this.scenario;
    }

    private void parseNumberOfTurns(int currentLine) throws BadFileException {
        try {
            if (this.scanner.hasNextLine()) {
                String firstLine = this.scanner.nextLine().trim();
                int turn = Integer.parseInt(firstLine);
                if (turn <= 0) {
                    throw new NumberFormatException();
                }
                this.scenario.setNumberOfTurns(turn);
            } else {
                this.scanner.close();
                throw new BadFileException("Empty");
            }
        } catch (NumberFormatException e) {
            this.scanner.close();
            throw new BadFileException("Line " + currentLine + ": Bad Number of Turn");
        }
    }

    private void parseFlyables(int currentLine) throws BadFileException {
        try {
            while(this.scanner.hasNextLine()) {
                String line = this.scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] flyableParameters = line.split("\\s+");
                if (flyableParameters.length != 5) {
                    throw new BadFileException("Line " + currentLine + ": Bad Flyable");
                }
                
                String type = flyableParameters[0];
                String name = flyableParameters[1];
                int longitude = Integer.parseInt(flyableParameters[2]);
                int latitude = Integer.parseInt(flyableParameters[3]);
                int height = Integer.parseInt(flyableParameters[4]);
                if (height < 0) {
                    height = 0;
                } else if (height > 100) {
                    height = 100;
                }
                Coordinates coordinate = new Coordinates(longitude, latitude, height);
                Flyable flyable = AircraftFactory.newAircraft(type, name, coordinate);
                this.scenario.addFlyable(flyable);
                currentLine++;
            }
        } catch (NumberFormatException e) {
            this.scanner.close();
            throw new BadFileException("Line " + currentLine + ": Bad Flyable");
        } catch (BadFlyableException e) {
            this.scanner.close();
            throw new BadFileException("Line " + currentLine + ": " + e.getMessage());
        }
    }

    public Scenario getScenario() {
        return this.scenario;
    }
}