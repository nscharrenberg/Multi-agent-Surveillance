package com.nscharrenberg.um.multiagentsurveillance.headless.models;

import java.util.Arrays;
import java.util.Optional;

public enum GameMode {
    EXPLORATION(0, "Exploration"),
    GUARD_INTRUDER_ALL(1, "Guard vs Intruder (All intruders must escape)"),
    GUARD_INTRUDER_ONE(2, "Guard vs Intruder (At least one intruders must escape)");

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

    public static GameMode getById(int id) {
        Optional<GameMode> resultOpt = Arrays.stream(GameMode.values()).filter(g -> g.getId() == id).findFirst();

        // Set default gamemode if none is found
        if (resultOpt.isEmpty()) {
            return GameMode.EXPLORATION;
        }

        return resultOpt.get();
    }
}
