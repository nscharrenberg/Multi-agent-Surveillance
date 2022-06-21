package com.nscharrenberg.um.multiagentsurveillance.headless.models;

public enum GameState {
    NO_RESULT("No results"),
    GUARDS_WON("Guards have won"),
    INTRUDERS_WON("Intruders have won");

    GameState(String message) {
        this.message = message;
    }

    final String message;

    public String getMessage() {
        return message;
    }
}
