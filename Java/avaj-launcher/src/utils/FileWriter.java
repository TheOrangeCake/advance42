package src.utils;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

public class FileWriter {
    public static final String outputFile = "simulation.txt";

    public static void writeLine(String text) {
        try {
            text += "\n";
            Files.write(Paths.get(outputFile), text.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Output File Error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            deleteOutFile();
            System.exit(1);
        }
    }

    public static void deleteOutFile() {
        try {
            Files.deleteIfExists(Paths.get(outputFile));
        } catch (IOException e) {
            System.out.println("Output File Error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            System.exit(1);
        }
    }
}