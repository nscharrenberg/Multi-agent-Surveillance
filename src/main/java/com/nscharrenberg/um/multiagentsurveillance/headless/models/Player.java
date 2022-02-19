package com.nscharrenberg.um.multiagentsurveillance.headless.models;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;

import java.util.Objects;

public abstract class Player extends Collision {
    private Angle direction;
    private double speed;
    private Area<Tile> observation;
    private Agent agent;

    // TODO: Keep track of the state the player is in (moving, standing still, climbing, on_target)

    public Player(Tile tile, Angle direction, double speed) {
        super(tile);
        this.direction = direction;
        this.speed = speed;
        this.agent = null;
    }

    public Player(Tile tile, Angle direction, double speed, Area<Tile> observation) {
        super(tile);
        this.direction = direction;
        this.speed = speed;
        this.observation = observation;
        this.agent = null;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Double.compare(player.speed, speed) == 0 && direction == player.direction && Objects.equals(observation, player.observation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(direction, speed, observation);
    }

    @Override
    public Agent getAgent() {
        return agent;
    }

    @Override
    public void setAgent(Agent agent) {
        this.agent = agent;
    }
}
