package com.nscharrenberg.um.multiagentsurveillance.headless.models;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;

public class Marker extends Item {
    private MarkerType type;
    private Tile tile;
    private int currentDuration;
    private Player player;

    public final int RANGE = 5;
    public final int DURATION = 10;

    public enum MarkerType {
        DEAD_END,
        TARGET,
        GUARD_SPOTTED,
        INTRUDER_SPOTTED,
        TELEPORTER,
        SHADED
    }

    //TODO: Drop marker whenever there is a teammate nearby --> keep kind of short term memory


    public Marker(MarkerType type, Tile tile, Player player) {
        super(tile);
        currentDuration = DURATION;
        this.type = type;
        this.tile = tile;
        this.player = player;
    }

    public MarkerType getType() {
        return type;
    }

    public void setType(MarkerType type) {
        this.type = type;
    }

    public int getRange() {
        return RANGE;
    }

    public int getCurrentDuration() {
        return currentDuration;
    }

    public Tile getTile() {
        return tile;
    }

    public void decrementCurrentDuration() {
        currentDuration--;
    }

    public Player getPlayer() {
        return player;
    }
}
