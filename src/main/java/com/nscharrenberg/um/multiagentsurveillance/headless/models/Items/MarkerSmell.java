package com.nscharrenberg.um.multiagentsurveillance.headless.models.Items;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.AdvancedAngle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Marker;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;


public class MarkerSmell extends Marker {

    private int strength;
    private AdvancedAngle direction;


    public MarkerSmell(Tile tile, Marker.MarkerType marking, int strength, AdvancedAngle direction, Player player, int duration) {
        super(marking, tile, player, duration);
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
