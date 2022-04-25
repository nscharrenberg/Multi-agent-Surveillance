package com.nscharrenberg.um.multiagentsurveillance.headless.models.Player;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

import java.util.Objects;

public class Intruder extends Player {
    private double sprintSpeed;
    private boolean isSprinting = false;

    public Intruder(Tile position, Angle direction) {
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


   /**
     * To make sure the intruder knows the direction of the target area
     * @param position - the position of the intruder
     * @return angle - the angle between the intruder and the target area
     * @throws RuntimeException - Thrown when there is no target direction provided for the intruder
     */
    public AdvancedAngle targetDirection(Tile position)
    {
        if (position == null) {
            throw new RuntimeException("no intruder position provided for intruder");
        }

        // Upper Left
        int upperLeftX = 20;
        int upperLeftY = 40;

        // Bottom Left
        int bottomLeftX = 20;
        int bottomLeftY = 45;

        // Upper Right
        int upperRightX = 25;
        int upperRightY = 40;

        // Bottom Right
        int bottomRightX = 25;
        int bottomRightY = 45;

        Tile targetCentre = new Tile((upperLeftX + bottomRightX)/2, (upperLeftY + bottomRightY)/2);

        int dX = targetCentre.getX() - position.getX();
        int dY = targetCentre.getY() - position.getY();

        // Normalization
        dX = Integer.compare(dX, 0);
        dY = Integer.compare(dY, 0);

        int pick;
        if(dX == 0 && dY == -1)
            pick = 0;
        else if(dX == 0 && dY == 1)
            pick = 1;
        else if(dX == -1 && dY ==0)
            pick = 2;
        else if(dX == 1 && dY == 0)
            pick = 3;
        else if(dX == -1 && dY == -1)
            pick = 4;
        else if(dX == 1 && dY == -1)
            pick = 5;
        else if(dX == -1 && dY == 1)
            pick = 6;
        else
            pick = 7;

        AdvancedAngle targetDirection = AdvancedAngle.values()[pick];

        if(targetDirection == null)
        {
            throw new RuntimeException("no targetDirection provided for intruder");
        }

        return targetDirection;
    }


}
