package com.nscharrenberg.um.multiagentsurveillance.headless.models;

public abstract class Item {
    private Tile tile;

    public Item(Tile tile) {
        this.tile = tile;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }
}
