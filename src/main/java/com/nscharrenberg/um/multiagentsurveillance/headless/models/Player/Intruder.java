package com.nscharrenberg.um.multiagentsurveillance.headless.models.Player;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.AdvancedAngle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.TargetDirection;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Intruder extends Player {
    private double sprintSpeed;
    private boolean isSprinting = false;
    private AdvancedAngle targetDirection;

    public Intruder(Tile position, AdvancedAngle direction) {
        // TODO: Read speed from Configuration
        super(position, direction, 10);

        // TODO: Read sprint speed from Configuration
        this.sprintSpeed = 20;

        // this.targetDirection = TargetDirection.computeTargetDirection(position.getX(), position.getY());
        this.targetDirection = TargetDirection.computeTargetDirectionInAdvancedAngle(position.getX(), position.getY());
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

    public AdvancedAngle getTargetDirection()
    {
        return targetDirection;
    }
    
}
