package com.nguyen.market.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TRANSACTION")
public class Transaction {
    @Id
    private Long id;

    @Column(name = "message")
    private String fixMessage;

    @Column(name = "status")
    private MessageStatus status;

    public Transaction() {

    }

    public Transaction(String fixMessage, Long id) {
        this.fixMessage = fixMessage;
        this.id = id;
        status = MessageStatus.PENDING;
    }

    public Long getId() {
        return id;
    }
    public String getFixMessage() {
        return fixMessage;
    }
    public MessageStatus getStatus() {
        return status;
    }

    public void setId(Long id) { this.id = id; }
    public void setFixMessage(String fixMessage) {
        this.fixMessage = fixMessage;
    }
    public void setStatus(MessageStatus status) {
        this.status = status;
    }
}
