package com.nscharrenberg.um.multiagentsurveillance.headless.models;

public class MarkerSmell extends Item {

    private ItemType marking;
    private int strength;
    private AdvancedAngle direction;


    // TODO: We need some way of distinguishing markers (and add it to itemtype)
    public MarkerSmell(Tile tile, ItemType marking, int strength, AdvancedAngle direction) {
        super(tile);
        this.marking = marking;
        this.strength = strength;
        this.direction = direction;
    }

    public ItemType getMarking() { return marking; }

    public void setMarking(ItemType marking) { this.marking = marking; }

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
