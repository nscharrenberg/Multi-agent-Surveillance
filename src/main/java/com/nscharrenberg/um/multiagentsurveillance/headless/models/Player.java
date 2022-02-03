package com.nscharrenberg.um.multiagentsurveillance.headless.models;

public class Player {
    private Tile position;
    private Angle direction;
    private double speed;

    // TODO: Keep track of the state the player is in (moving, standing still, climbing, on_target)

    public Player(Tile position, Angle direction, double speed) {
        this.position = position;
        this.direction = direction;
        this.speed = speed;
    }

    public Tile getPosition() {
        return position;
    }

    public void setPosition(Tile position) {
        this.position = position;
    }

    public Angle getDirection() {
        return direction;
    }

    public void setDirection(Angle direction) {
        this.direction = direction;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
