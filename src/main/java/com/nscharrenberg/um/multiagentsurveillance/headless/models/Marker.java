package com.nscharrenberg.um.multiagentsurveillance.headless.models;

public class Marker extends Item {
    private MarkerType type;
    private Tile tile;
    private int currentDuration;
    public final int RANGE = 5;
    public final int DURATION = 10;

    public enum MarkerType {
        DEAD_END,
        TARGET,
        GUARD_SPOTTED,
        INTRUDER_SPOTTED,
        TELEPORTER,
        SHADED
    }

    //TODO: Drop marker whenever there is a teammate nearby --> keep kind of short term memory


    public Marker(MarkerType type, Tile tile) {
        super(tile);
        currentDuration = DURATION;
        this.tile = tile;
        this.type = type;
    }

    public MarkerType getType() {
        return type;
    }

    public void setType(MarkerType type) {
        this.type = type;
    }

    public int getRange() {
        return RANGE;
    }

    public int getCurrentDuration() {
        return currentDuration;
    }

    public Tile getTile() {
        return tile;
    }

    public void decrementCurrentDuration() {
        currentDuration--;
    }
}
