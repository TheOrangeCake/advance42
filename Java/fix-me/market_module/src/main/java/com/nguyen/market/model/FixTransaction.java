package com.nguyen.market.model;

import jakarta.persistence.*;

@Entity
@Table(name = "MARKET_ORDERS")
public class FixTransaction {
    @Id
    private Long id;

    @Column(name = "market_id")
    private String marketId;

    @Column(name = "request")
    private String fixRequestMessage;

    @Column(name = "response")
    private String fixResponseMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MessageStatus status;

    public FixTransaction() {

    }

    public FixTransaction(String fixRequestMessage, Long id) {
        this.fixRequestMessage = fixRequestMessage;
        this.id = id;
        status = MessageStatus.PENDING;
    }

    public Long getId() {
        return id;
    }
    public String getMarketId() {
        return marketId;
    }
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
    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }
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
