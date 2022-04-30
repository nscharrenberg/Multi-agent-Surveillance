package com.nscharrenberg.um.multiagentsurveillance.headless.models;

public class Marker {
    private MarkerType type;
    private final int RANGE = 5;
    private final int DURATION = 10;

    public enum MarkerType {
        DEAD_END,
        TARGET,
        GUARD_SPOTTED,
        INTRUDER_SPOTTED,
        TELEPORTER,
        SHADED
    }

    //TODO: Drop marker whenever there is a teammate nearby --> keep kind of short term memory


    public Marker(MarkerType type) {
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

    public int getDuration() {
        return DURATION;
    }

//    public AdvancedAngle getDirection() { return direction; }
//
//    public void setDirection(AdvancedAngle direction) { this.direction = direction;}
}
