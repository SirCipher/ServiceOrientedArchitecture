package com.type2labs.nevernote.jpa.entity;

/**
 * Enum denoting the access level for sharing a {@link Notebook} with another {@link User}
 */
public enum AccessLevel {

    READ("READ"), READ_WRITE("READ_WRITE");

    private String value;

    AccessLevel(String value) {
        this.value = value;
    }

    public static AccessLevel from(String s) {
        for (AccessLevel a : AccessLevel.values()) {
            if (s.toUpperCase().equalsIgnoreCase(s)) {
                return a;
            }
        }

        throw new IllegalArgumentException("No matching AccessLevel for: " + s);
    }

    public String getValue() {
        return value;
    }

}
