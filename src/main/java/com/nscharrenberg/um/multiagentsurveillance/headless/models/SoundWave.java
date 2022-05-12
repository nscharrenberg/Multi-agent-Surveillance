package com.nscharrenberg.um.multiagentsurveillance.headless.models;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.AdvancedAngle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;


public class SoundWave extends Item {
    private int strength; // Sound strength
    private AdvancedAngle direction; // Sound direction (where it is coming from)

    public SoundWave(Tile tile, int strength, AdvancedAngle direction) {
        super(tile);
        this.strength = strength;
        this.direction = direction;
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
}
