package com.nscharrenberg.um.multiagentsurveillance.headless.models;

public class MarkerIntruder {
    private MarkerIntruderType type;
    private final int RANGE = 5;
    private final int DURATION = 10;
    private AdvancedAngle direction;

    public enum MarkerIntruderType {
        DEAD_END,
        TARGET,
        GUARD_SPOTTED,
        GUARD_STRUCTURE,
        TELEPORTER
    }


    public MarkerIntruder(MarkerIntruderType type, AdvancedAngle direction) {
        this.type = type;
        this.direction = direction;
    }

    public MarkerIntruderType getType() {
        return type;
    }

    public void setType(MarkerIntruderType type) {
        this.type = type;
    }

    public int getRange() {
        return RANGE;
    }

//    public void setRange(int RANGE) {
//       this.RANGE = RANGE;
//    }

    public int getDuration() {
        return DURATION;
    }

//    public void setDuration(int DURATION) {
//        this.DURATION = DURATION;
//    }

    public AdvancedAngle getDirection() { return direction; }

    public void setDirection(AdvancedAngle direction) { this.direction = direction;}
}
