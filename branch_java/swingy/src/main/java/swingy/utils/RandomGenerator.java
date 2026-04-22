package swingy.utils;

import java.util.Random;

public class RandomGenerator {
    private static Random INSTANCE;

    private RandomGenerator() {

    }

    public static Random getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Random();
        }
        return INSTANCE;
    }
}
