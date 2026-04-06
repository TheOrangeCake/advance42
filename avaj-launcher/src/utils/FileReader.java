package src.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Scanner;
import src.exceptions.BadFileException;
import src.exceptions.BadFlyableException;
import src.exceptions.BadProgrammerException;
import src.flyables.aircrafts.AircraftFactory;
import src.flyables.Flyable;

public class FileReader {
    private Scanner scanner;

    public FileReader(String fileName) throws BadFileException {
        Path path = Paths.get(fileName);
        try {
            this.scanner = new Scanner(path);
        } catch(IOException e) {
            throw new BadFileException(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public void createScenario() throws BadFileException, BadProgrammerException {
        if (this.scanner == null) {
            throw new BadProgrammerException();
        }
        int currentLine = 1;
        parseNumberOfTurns(currentLine++);
        parseFlyables(currentLine);
        this.scanner.close();
        if (Scenario.getFlyables().isEmpty()) {
            throw new BadFileException("No flyables");
        }
    }

    private void parseNumberOfTurns(int currentLine) throws BadFileException {
        try {
            if (this.scanner.hasNextLine()) {
                String firstLine = this.scanner.nextLine().trim();
                int turn = Integer.parseInt(firstLine);
                if (turn <= 0) {
                    throw new NumberFormatException();
                }
                Scenario.setNumberOfTurns(turn);
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
                if (longitude < 0) {
                    throw new BadFileException("Line " + currentLine + ": Bad Flyable");
                }
                int latitude = Integer.parseInt(flyableParameters[3]);
                if (latitude < 0) {
                    throw new BadFileException("Line " + currentLine + ": Bad Flyable");
                }
                int height = Integer.parseInt(flyableParameters[4]);
                if (height < 0) {
                    throw new BadFileException("Line " + currentLine + ": Bad Flyable");
                } else if (height > 100) {
                    height = 100;
                }
                Coordinates coordinate = new Coordinates(longitude, latitude, height);
                Flyable flyable = AircraftFactory.newAircraft(type, name, coordinate);
                Scenario.addFlyable(flyable);
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

}