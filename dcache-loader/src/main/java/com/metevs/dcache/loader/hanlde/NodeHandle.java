package com.metevs.dcache.loader.hanlde;

import lombok.Data;

/**
 *
 */
@Data
public abstract class NodeHandle<T> implements Node {

    private Integer position;

    /**
     * @param pos
     */
    public void setPosition(Integer pos) {
        position = pos;
    }


    public NodeHandle() {

    }

    private NodeHandle<T> next;


    @Override
    public void setNext(Node node) {
        this.next = (NodeHandle) node;
    }

}
