package com.nscharrenberg.um.multiagentsurveillance.headless.models;

public abstract class Player extends Collision {
    private Angle direction;
    private double speed;
    private Area<Tile> observation;

    // TODO: Keep track of the state the player is in (moving, standing still, climbing, on_target)

    public Player(Tile tile, Angle direction, double speed) {
        super(tile);
        this.direction = direction;
        this.speed = speed;
    }

    public Player(Tile tile, Angle direction, double speed, Area<Tile> observation) {
        super(tile);
        this.direction = direction;
        this.speed = speed;
        this.observation = observation;
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

    public Area<Tile> getObservation() {
        return observation;
    }

    public void setObservation(Area<Tile> observation) {
        this.observation = observation;
    }
}