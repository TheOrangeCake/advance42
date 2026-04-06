package src.weathers.towers;

import java.util.ArrayList;
import java.util.List;
import src.flyables.Flyable;
import src.exceptions.BadWeatherException;
import src.exceptions.BadProgrammerException;
import src.utils.FileWriter;

public class Tower {
    private List<Flyable> observers;

    public Tower() {
        this.observers = new ArrayList<>();
    }

    public void register(Flyable p_flyable) {
        this.observers.add(p_flyable);
        FileWriter.writeLine("Tower says: " +
                            p_flyable.getFlyableModel() +
                            " registered to weather tower.");
    }

    public void unregister(Flyable p_flyable) {
        this.observers.remove(p_flyable);
        FileWriter.writeLine("Tower says: " +
                            p_flyable.getFlyableModel() +
                            " unregistered from weather tower.");
    }

    protected void conditionChanged() {
        List<Flyable> copy = new ArrayList<>(this.observers);
        for (Flyable flyable : copy) {
            try {
                flyable.updateConditions();
            } catch (BadWeatherException | BadProgrammerException e) {
                System.out.println(e.getMessage());
                FileWriter.deleteOutFile();
                System.exit(1);
            }
        }
        FileWriter.writeLine("-------------------------------------");
    }

    public List<Flyable> getObservers() {
        return this.observers;
    }
}