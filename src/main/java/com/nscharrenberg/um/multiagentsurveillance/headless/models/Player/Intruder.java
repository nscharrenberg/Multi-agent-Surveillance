package com.nscharrenberg.um.multiagentsurveillance.headless.models.Player;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.CalculateDistance;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.ManhattanDistance;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.targetPositionCalculator.TargetPositionCalculator;

import java.util.Objects;

public class Intruder extends Player {
    private double sprintSpeed;
    private boolean isSprinting = false;

    private Action targetAngle;
    private double distanceToTarget = Double.MAX_VALUE;
    private Tile target;
    private final TargetPositionCalculator targetPositionCalculator;
    private final CalculateDistance calculateDistance = new ManhattanDistance();

    public Intruder(Tile position, Action direction) {
        // TODO: Read speed from Configuration
        super(position, direction, 10);

        this.targetPositionCalculator = new TargetPositionCalculator();
        // TODO: Read sprint speed from Configuration
        this.sprintSpeed = 20;
    }

    public Action getTargetAngle() {
        return targetAngle;
    }

    public double getDistanceToTarget(){
        return this.distanceToTarget;
    }

    public Tile getTarget(){
        return this.target;
    }

    public void updateTargetInfo() {
        this.targetAngle = Factory.getGameRepository().getTargetGameAngle(this);

        if(this.target == null) {
            this.target = targetPositionCalculator.calculate(Factory.getGameRepository().getTargetRealAngle(this),
                    getTile());
        } else {
            this.distanceToTarget = this.calculateDistance.compute(this.target, getTile());
        }
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
