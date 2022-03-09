package com.nscharrenberg.um.multiagentsurveillance.headless.models;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(tile, item.tile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tile);
    }
}
