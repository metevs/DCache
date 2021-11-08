package com.metevs.dcache.loader.canal.protocol;

public enum CanalEventType {
    /**
     * <code>INSERT = 1;</code>
     */
    INSERT(1),
    /**
     * <code>UPDATE = 2;</code>
     */
    UPDATE(2),
    /**
     * <code>DELETE = 3;</code>
     */
    DELETE(3),
    /**
     * <code>CREATE = 4;</code>
     */
    CREATE(4),
    /**
     * <code>ALTER = 5;</code>
     */
    ALTER(5),
    /**
     * <code>ERASE = 6;</code>
     */
    ERASE(6),
    /**
     * <code>QUERY = 7;</code>
     */
    QUERY(7),
    /**
     * <code>TRUNCATE = 8;</code>
     */
    TRUNCATE(8),
    /**
     * <code>RENAME = 9;</code>
     */
    RENAME(9),
    /**
     * <code>CINDEX = 10;</code>
     *
     * <pre>
     * *CREATE INDEX*
     * </pre>
     */
    CINDEX(10),
    /**
     * <code>DINDEX = 11;</code>
     */
    DINDEX(11),
    /**
     * <code>GTID = 12;</code>
     */
    GTID(12),
    /**
     * <code>XACOMMIT = 13;</code>
     *
     * <pre>
     * * XA *
     * </pre>
     */
    XACOMMIT(13),
    /**
     * <code>XAROLLBACK = 14;</code>
     */
    XAROLLBACK(14),
    /**
     * <code>MHEARTBEAT = 15;</code>
     *
     * <pre>
     * * MASTER HEARTBEAT *
     * </pre>
     */
    MHEARTBEAT(15),
    ;

    private final int index;

    CanalEventType(int index) {
        this.index = index;
    }

    public static CanalEventType valueOf(int value) {
        switch (value) {
            case 1:
                return INSERT;
            case 2:
                return UPDATE;
            case 3:
                return DELETE;
            case 4:
                return CREATE;
            case 5:
                return ALTER;
            case 6:
                return ERASE;
            case 7:
                return QUERY;
            case 8:
                return TRUNCATE;
            case 9:
                return RENAME;
            case 10:
                return CINDEX;
            case 11:
                return DINDEX;
            case 12:
                return GTID;
            case 13:
                return XACOMMIT;
            case 14:
                return XAROLLBACK;
            case 15:
                return MHEARTBEAT;
            default:
                return null;
        }
    }

}
