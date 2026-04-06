package src.utils;

import src.flyables.Flyable;
import java.util.List;
import java.util.ArrayList;

public class Scenario {
    private static Scenario instance;
    private static List<Flyable> flyables;
    private static int numberOfTurns;

    private Scenario() {
        flyables = new ArrayList<>();
        numberOfTurns = 0;
    }

    public static Scenario getInstance() {
        if (instance == null) {
            instance = new Scenario();
        }
        return instance;
    }

    public static void setNumberOfTurns(int number) {
        numberOfTurns = number;
    }

    public static int getNumberOfTurns() {
        return numberOfTurns;
    }

    public static void addFlyable(Flyable flyable) {
        flyables.add(flyable);
    }

    public static void removeFlyable(Flyable flyable) {
        flyables.remove(flyable);
    }

    public static List<Flyable> getFlyables() {
        return flyables;
    }

    public static void printFlyable() {
        for (Flyable flyable : flyables) {
            System.out.println(
                "ID: " + flyable.getId() +
                ",\tName: " + flyable.getName() +
                ",\t" + flyable.getCoordinate().toString()
            );
        }
    }
}