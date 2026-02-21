package com.giggr.identity.domain.identity;

public enum EntityType {

    INDIVIDUAL("101"),
    INDUSTRY("201"),
    INSTITUTION("301");

    private final String code;

    EntityType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
