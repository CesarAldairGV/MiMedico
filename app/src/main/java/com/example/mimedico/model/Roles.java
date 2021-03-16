package com.example.mimedico.model;

import lombok.Getter;

public enum Roles {
    ADMIN("ADMIN"),
    MEDIC("MEDIC"),
    USER("USER");
    @Getter
    private String role;
    Roles(String role) {
        this.role = role;
    }
}
