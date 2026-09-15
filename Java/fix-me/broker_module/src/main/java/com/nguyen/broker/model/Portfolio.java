package com.nguyen.broker.model;

import com.nguyen.colors.Colors;
import java.util.HashMap;
import java.util.Map;

public class Portfolio {
    private final Map<String, Double> portfolio = new HashMap<>();

    public Portfolio() {

    }

    public void add(String symbol, Double quantity) {
        portfolio.merge(symbol, quantity, Double::sum);
    }

    public void remove(String symbol, Double quantity) {
        Double current = portfolio.get(symbol);
        if (current == null) {
            return;
        }
        double updated = current - quantity;
        if (updated <= 0) {
            portfolio.remove(symbol);
        } else {
            portfolio.put(symbol, updated);
        }
    }

    public void printPortfolio() {
        if (portfolio.isEmpty()) {
            System.out.println(Colors.YELLOW + "Portfolio is empty" + Colors.RESET);
            return;
        }
        System.out.println("-------------------------");
        System.out.printf("%-8s %-10s%n", "Symbol", "Quantity");
        System.out.println("-".repeat(20));
        for (Map.Entry<String, Double> entry : portfolio.entrySet()) {
            System.out.printf("%-8s %-10.2f%n", entry.getKey(), entry.getValue());
        }
        System.out.println("-------------------------");
    }
}
