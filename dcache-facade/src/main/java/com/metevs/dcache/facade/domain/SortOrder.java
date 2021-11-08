package com.metevs.dcache.facade.domain;


/**
 *
 */
public enum SortOrder {
    ASC {
        public String toString() {
            return "asc";
        }
    },
    DESC {
        public String toString() {
            return "desc";
        }
    };

    private SortOrder() {
    }
}