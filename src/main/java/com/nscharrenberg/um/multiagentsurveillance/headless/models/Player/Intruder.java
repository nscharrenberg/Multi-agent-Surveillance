package com.nscharrenberg.um.multiagentsurveillance.headless.models.Player;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

import java.util.Objects;

public class Intruder extends Player {
    private double sprintSpeed;
    private boolean isSprinting = false;


    public Intruder(Tile position, Action direction) {
        // TODO: Read speed from Configuration
        super(position, direction, 10);

        // TODO: Read sprint speed from Configuration
        this.sprintSpeed = 20;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Intruder intruder = (Intruder) o;
        return Double.compare(intruder.sprintSpeed, sprintSpeed) == 0 && isSprinting == intruder.isSprinting;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sprintSpeed, isSprinting);
    }
}
