package com.nguyen.market.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "FLAG_TABLE")
public class FirstFlag {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;
}
