package com.nguyen.market.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

@Entity
@Table(name = "INSTRUMENTS")
public class Instrument {
    @Id
    private String symbol;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "stock")
    @PositiveOrZero
    private double stock;

    @Column(name = "price")
    @PositiveOrZero
    private double price;

    public Instrument() {
    }

    public Instrument(
            String symbol,
            String name,
            double stock,
            double price
    ) {
        this.symbol = symbol;
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    public static void printAll(List<Instrument> instruments) {
        System.out.printf("%-8s %-20s %-10s %-10s%n", "Symbol", "Name", "Stock", "Price");
        System.out.println("-".repeat(50));
        for (Instrument i : instruments) {
            System.out.printf(
                    "%-8s %-20s %-10.2f %-10.2f%n", i.getSymbol(), i.getName(), i.getStock(), i.getPrice());
        }
    }

    public void decreaseStock(double quantity) {
        stock -= quantity;
        if (stock < 0) {
            stock = 0;
        }
    }

    public void increaseStock(double quantity) {
        stock += quantity;
    }

    public String getSymbol() {
        return symbol;
    }
    public String getName() {
        return name;
    }
    public double getStock() {
        return stock;
    }
    public double getPrice() {
        return price;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setStock(double stock) {
        this.stock = stock;
    }
    public void setPrice(double price) {
        this.price = price;
    }
}
