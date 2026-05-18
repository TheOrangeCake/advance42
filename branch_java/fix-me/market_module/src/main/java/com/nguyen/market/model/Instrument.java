package com.nguyen.market.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "INSTRUMENT")
public class Instrument {
    @Id
    private String symbol;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "stock")
    @PositiveOrZero
    @NotNull
    private double stock;

    @Column(name = "price")
    @PositiveOrZero
    @NotNull
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
