package com.nscharrenberg.um.multiagentsurveillance.headless.models;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;

public abstract class Collision extends Item {
    public Collision(Tile tile) {
        super(tile);
    }

    public abstract Agent getAgent();

    public abstract void setAgent(Agent agent);
}
