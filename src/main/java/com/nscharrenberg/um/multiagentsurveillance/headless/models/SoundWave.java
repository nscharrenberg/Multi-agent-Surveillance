package com.nscharrenberg.um.multiagentsurveillance.headless.models;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.AdvancedAngle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;


public class SoundWave extends Item {
    private int strength; // Sound strength
    private AdvancedAngle direction; // Sound direction (where it is coming from)
    private Player owner;

    public SoundWave(Tile tile, int strength, AdvancedAngle direction, Player owner) {
        super(tile);
        this.strength = strength;
        this.direction = direction;
        this.owner = owner;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public AdvancedAngle getDirection() {
        return direction;
    }

    public void setDirection(AdvancedAngle direction) {
        this.direction = direction;
    }

    public Player getOwner() { return owner; }

    public void setOwner(Player owner) { this.owner = owner; }

}
