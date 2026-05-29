package com.nguyen.message.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "MESSAGE_UID_COUNTER")
public class UidCounter {
    @Id
    private Integer id;

    @Column(name = "next_uid")
    private long nextUid;

    public UidCounter() {
    }

    public UidCounter(Integer id, long nextUid) {
        this.id = id;
        this.nextUid = nextUid;
    }

    public Integer getId() {
        return id;
    }

    public long getNextUid() {
        return nextUid;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNextUid(long nextUid) {
        this.nextUid = nextUid;
    }
}
