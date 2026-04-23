package swingy.model.map;

public class Map {
    private int level;
    private int size;
    private final int[] heroPosition = {0, 0};

    public Map(int level) {
        if (level <= 0) {
            level = 1;
        }
        this.level = level;
        this.size = (level - 1) * 5 + 10 - (level % 2);
    }

    public void moveHero(int horizontal, int vertical) {
        int x = Math.clamp(horizontal, 0, 1);
        int y = Math.clamp(vertical, 0, 1);
    }

    public int getLevel() {
        return this.level;
    }
}
