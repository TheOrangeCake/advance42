package src.utils;

import src.flyables.Flyable;
import java.util.List;
import java.util.ArrayList;

public class Scenario {
    private List<Flyable> flyables;
    private int numberOfTurns;

    public Scenario() {
        this.flyables = new ArrayList<>();
        this.numberOfTurns = 0;
    }

    public void setNumberOfTurns(int number) {
        this.numberOfTurns = number;
    }

    public int getNumberOfTurns() {
        return this.numberOfTurns;
    }

    public void addFlyable(Flyable flyable) {
        this.flyables.add(flyable);
    }

    public void removeFlyable(Flyable flyable) {
        this.flyables.remove(flyable);
    }

    public List<Flyable> getFlyables() {
        return this.flyables;
    }

    public void printFlyable() {
        for (Flyable flyable : this.flyables) {
            System.out.println(
                "ID: " + flyable.getId() +
                ",\tName: " + flyable.getName() +
                ",\t" + flyable.getCoordinate().toString()
            );
        }
    }
}