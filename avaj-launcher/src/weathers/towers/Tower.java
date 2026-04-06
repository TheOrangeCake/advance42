package src.weathers.towers;

import java.util.ArrayList;
import java.util.List;
import src.flyables.Flyable;
import src.exceptions.BadWeatherException;

public class Tower {
    private List<Flyable> observers;

    public Tower() {
        this.observers = new ArrayList<>();
    }

    public void register(Flyable p_flyable) {
        this.observers.add(p_flyable);
        System.out.println("Tower says: " +
                            p_flyable.getType() +
                            "#" +
                            p_flyable.getName() +
                            "(" +
                            p_flyable.getId() +
                            ") registered to weather tower.");
    }

    public void unregister(Flyable p_flyable) {
        this.observers.remove(p_flyable);
        System.out.println("Tower says: " +
                            p_flyable.getType() +
                            "#" +
                            p_flyable.getName() +
                            "(" +
                            p_flyable.getId() +
                            ") unregistered from weather tower.");
    }

    protected void conditionChanged() {
        for (Flyable flyable : this.observers) {
            try {
                flyable.updateConditions();
            } catch (BadWeatherException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
    }
}