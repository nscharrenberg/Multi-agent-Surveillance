package com.nscharrenberg.um.multiagentsurveillance.headless.models;

public enum GameMode {
    NO_COMMUNICATION(0, "No Communication");

    GameMode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private final int id;
    private final String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
