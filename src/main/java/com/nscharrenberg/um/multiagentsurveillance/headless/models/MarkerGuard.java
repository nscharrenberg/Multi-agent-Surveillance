package com.nscharrenberg.um.multiagentsurveillance.headless.models;

public class MarkerGuard {
    private MarkerGuardType type;
    private final int RANGE = 5;
    private final int DURATION = 10;
    private AdvancedAngle direction;

    public enum MarkerGuardType {
        DEAD_END,
        TARGET,
        INTRUDER_SPOTTED,
        GUARD_STRUCTURE,
        TELEPORTER
    }


    public MarkerGuard(MarkerGuardType type, AdvancedAngle direction) {
        this.type = type;
        this.direction = direction;
    }

    public MarkerGuardType getType() {
        return type;
    }

    public void setType(MarkerGuardType type) {
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
