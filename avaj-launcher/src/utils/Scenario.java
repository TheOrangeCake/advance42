package src.utils;

import src.flyables.Flyable;
import java.util.List;
import java.util.ArrayList;

public class Scenario {
    private List<Flyable> flyables;
    private int numberOfTurn;

    public Scenario() {
        this.flyables = new ArrayList<>();
        this.numberOfTurn = 0;
    }

    public void setNumberOfTurn(int number) {
        this.numberOfTurn = number;
    }

    public int getNumberOfTurn() {
        return this.numberOfTurn;
    }

    public void addFlyable(Flyable flyable) {
        this.flyables.add(flyable);
    }

    public void removeFlyable(Flyable flyable) {
        this.flyables.remove(flyable);
    }

    public void printFlyable() {
        for (Flyable flyable : this.flyables) {
            System.out.println(
                "ID: " + flyable.getId() +
                ",\tName: " + flyable.getName() +
                ",\t" + flyable.getcoordinate().toString()
            );
        }
    }
}