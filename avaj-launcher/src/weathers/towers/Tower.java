package src.weathers.towers;

import java.util.ArrayList;
import java.util.List;
import src.flyables.Flyable;

public class Tower {
    private List<Flyable> observers;

    public Tower() {
        this.observers = new ArrayList<>();
    }

    public void register(Flyable p_flyable) {

    }

    public void unregister(Flyable p_flyable) {

    }

    protected void conditionChanged() {
        
    }
}