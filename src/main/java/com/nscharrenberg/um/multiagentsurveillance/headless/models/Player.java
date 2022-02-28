package com.nscharrenberg.um.multiagentsurveillance.headless.models;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;

import java.util.Objects;
import java.util.UUID;

public abstract class Player extends Collision {
    private UUID id;
    private Angle direction;
    private double speed;
    private Area<Tile> vision;
    private Agent agent;

    // TODO: Keep track of the state the player is in (moving, standing still, climbing, on_target)

    public Player(Tile tile, Angle direction, double speed) {
        super(tile);
        this.id = UUID.randomUUID();
        this.direction = direction;
        this.speed = speed;
        this.agent = null;
    }

    public Player(Tile tile, Angle direction, double speed, Area<Tile> observation) {
        super(tile);
        this.id = UUID.randomUUID();
        this.direction = direction;
        this.speed = speed;
        this.vision = observation;
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

    public Area<Tile> getVision() {
        return vision;
    }

    public void setVision(Area<Tile> vision) {
        this.vision = vision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Player player = (Player) o;
        return Double.compare(player.speed, speed) == 0 && direction == player.direction && Objects.equals(vision, player.vision) && Objects.equals(agent, player.agent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), direction, speed, vision, agent);
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public UUID getUuid() {
        return id;
    }

    public String getId() {
        return id.toString();
    }
}
