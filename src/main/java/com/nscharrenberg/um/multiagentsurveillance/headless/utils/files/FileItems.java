package com.nscharrenberg.um.multiagentsurveillance.headless.utils.files;

public enum FileItems {
    NAME("name"),
    GAME_MODE("gameMode"),
    HEIGHT("height"),
    WIDTH("width"),
    SCALING("scaling"),
    NUM_GUARDS("numGuards"),
    NUM_INTRUDERS("numIntruders"),
    BASE_SPEED_INTRUDER("baseSpeedIntruder"),
    SPRINT_SPEED_INTRUDER("sprintSpeedIntruder"),
    BASE_SPEED_GUARD("baseSpeedGuard"),
    TIME_STEP("timeStep"),
    TARGET_AREA("targetArea"),
    SPAWN_AREA_INTRUDERS("spawnAreaIntruders"),
    SPAWN_AREA_GUARDS("spawnAreaGuards"),
    WALL("wall"),
    TELEPORT("teleport"),
    SHADED("shaded"),
    TEXTURE("texture"),
    DISTANCE_VIEWING("distanceViewing"),
    DISTANCE_HEARING("distanceHearing"),
    DISTANCE_SMELLING("distanceSmelling"),
    DISTANCE_LIGHT("distanceLight");

    FileItems(String key) {
        this.key = key;
    }

    private String key;

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return getKey();
    }
}
