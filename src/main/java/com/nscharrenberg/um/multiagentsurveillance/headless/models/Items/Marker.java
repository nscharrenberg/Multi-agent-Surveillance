package com.nscharrenberg.um.multiagentsurveillance.headless.models.Items;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;

public class Marker extends Item {
    private static final int RANGE = 5;
    private final int DURATION = 10;

    private MarkerType type;
    private Tile tile;
    private int currentDuration;
    private Player player;


    public enum MarkerType {
        DEAD_END,
        TARGET,
        GUARD_SPOTTED,
        INTRUDER_SPOTTED,
        TELEPORTER,
        SHADED
    }

    public Marker(MarkerType type, Tile tile, Player player, int duration) {
        super(tile);
        this.currentDuration = 150;
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

    public static int getRange() {
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
