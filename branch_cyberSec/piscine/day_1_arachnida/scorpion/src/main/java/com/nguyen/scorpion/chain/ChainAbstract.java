package com.nguyen.scorpion.chain;

import lombok.Getter;

@Getter
public abstract class ChainAbstract implements Chain {
    protected ChainAbstract next;

    public ChainAbstract setNext(ChainAbstract next) {
        this.next = next;
        return next;
    }
}
