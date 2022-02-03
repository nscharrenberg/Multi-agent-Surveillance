package com.nscharrenberg.um.multiagentsurveillance.headless.models;

public class Intruder extends Player {
    private Area<Tile> target;
    private double sprintSpeed;
    private boolean isSprinting = false;

    public Intruder(Tile position, Angle direction, Area<Tile> target) {
        // TODO: Read speed from Configuration
        super(position, direction, 10);

        // TODO: Read sprint speed from Configuration
        this.sprintSpeed = 20;
        this.target = target;



    }

    public Area<Tile> getTarget() {
        return target;
    }

    public void setTarget(Area<Tile> target) {
        this.target = target;
    }

    public double getSprintSpeed() {
        return sprintSpeed;
    }

    public void setSprintSpeed(double sprintSpeed) {
        this.sprintSpeed = sprintSpeed;
    }

    public boolean isSprinting() {
        return isSprinting;
    }

    public void setSprinting(boolean sprinting) {
        isSprinting = sprinting;
    }

    @Override
    public double getSpeed() {
        if (isSprinting) {
            return sprintSpeed;
        }

        return super.getSpeed();
    }
}
