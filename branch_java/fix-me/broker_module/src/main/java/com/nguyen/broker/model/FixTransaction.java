package com.nguyen.broker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "BROKER_ORDERS")
public class FixTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "broker_id")
    private String brokerId;

    @Column(name = "request")
    private String fixRequestMessage;

    @Column(name = "response")
    private String fixResponseMessage;

    @Column(name = "status")
    private MessageStatus status;

    public FixTransaction() {
        status = MessageStatus.PENDING;
    }

    public FixTransaction(String fixRequestMessage) {
        this.fixRequestMessage = fixRequestMessage;
        status = MessageStatus.PENDING;
    }

    public Long getId() {
        return id;
    }
    public String getBrokerId() { return brokerId; }
    public String getFixRequestMessage() {
        return fixRequestMessage;
    }
    public String getFixResponseMessage() {
        return fixResponseMessage;
    }
    public MessageStatus getStatus() {
        return status;
    }

    public void setId(Long id) { this.id = id; }
    public void setBrokerId(String brokerId) { this.brokerId = brokerId; }
    public void setFixRequestMessage(String fixRequestMessage) {
        this.fixRequestMessage = fixRequestMessage;
    }
    public void setFixResponseMessage(String fixResponseMessage) {
        this.fixResponseMessage = fixResponseMessage;
    }
    public void setStatus(MessageStatus status) {
        this.status = status;
    }
}
