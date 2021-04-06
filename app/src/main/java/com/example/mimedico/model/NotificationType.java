package com.example.mimedico.model;

public enum NotificationType {
    MEDIC_MESSAGE("MEDIC_MESSAGE"),
    USER_ACCEPT("USER_ACCEPT");
    private String type;
    NotificationType(String type){
        this.type = type;
    }
    public String getType(){
        return type;
    }
}
