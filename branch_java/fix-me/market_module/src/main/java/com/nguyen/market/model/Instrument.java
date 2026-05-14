package com.nguyen.market.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "INSTRUMENT")
public class Instrument {
    @Id
    private String symbol;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "stock")
    @Min(0)
    @NotNull
    private int stock;

    @Column(name = "price")
    @Min(0)
    @NotNull
    private double price;

    public String getSymbol() {
        return symbol;
    }
    public String getName() {
        return name;
    }
    public int getStock() {
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
    public void setStock(int stock) {
        this.stock = stock;
    }
    public void setPrice(double price) {
        this.price = price;
    }
}
