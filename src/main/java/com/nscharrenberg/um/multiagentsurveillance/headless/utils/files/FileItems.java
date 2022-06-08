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
    DEADEND_MARKER("deadendMarker"),

    WALL_TEXTURE("wallTexture"),
    FLOOR_TEXTURE("floorTexture"),
    GUARD_SPAWN_TEXTURE("guardSpawnTexture"),
    INTRUDER_SPAWN_TEXTURE("intruderSpawnTexture"),
    TELEPORT_DESTINATION_TEXTURE("teleportDestinationTexture"),
    TELEPORT_SOURCE_TEXTURE("teleportSourceTexture"),
    TARGET_AREA_TEXTURE("targetAreaTexture"),
    SHADED_TEXTURE("shadedTexture"),
    MAP_DATA("map"),
    DISTANCE_SOUND_WALKING("distanceSoundWalking"),
    DISTANCE_SOUND_ROTATING("distanceSoundRotating"),
    DISTANCE_SOUND_SPRINTING("distanceSoundSprinting"),
    DISTANCE_SOUND_WAITING("distanceSoundWaiting"),
    DISTANCE_SOUND_YELLING("distanceSoundYelling"),
    DISTANCE_VIEWING("distanceViewing");



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
