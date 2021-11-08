package com.metevs.dcache.facade.util;

public interface QueryType {
    enum  CompareType {
        GT,
        LT,
        GTE,
        LTE,
        BT;

        CompareType() {
        }
    }

    enum NullType {
        ISNULL,
        NOTNULL,
        NULL_OR_DEFAULT,
        NOTNULL_OR_DEFAULT,
        ;

        NullType() {
        }
    }

    enum EqualType {
        EQUAL,
        NOT_EQUAL;

        EqualType() {
        }
    }

}
