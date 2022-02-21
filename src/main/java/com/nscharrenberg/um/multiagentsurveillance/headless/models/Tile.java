package com.nscharrenberg.um.multiagentsurveillance.headless.models;

import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tile {
    private int x;
    private int y;
    private List<Item> items;

    public Tile() {
        items = new ArrayList<>();
    }

    public Tile(List<Item> items) {
        this.items = items;
    }

    public Tile(int x, int y, List<Item> items) {
        this.x = x;
        this.y = y;
        this.items = items;
    }

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
        this.items = new ArrayList<>();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public boolean isCollision() {
        boolean blocked = false;
        for (Item item : items) {
            if (item instanceof Collision) {
                blocked = true;
                break;
            }
        }

        return blocked;
    }

    /**
     * Add Item to tile
     * @param item - the item to be added
     * @throws ItemAlreadyOnTileException - Thrown when item is already on this tile.
     */
    public void add(Item item) throws ItemAlreadyOnTileException {
        if (items.contains(item)) {
            return;
        }

        if (item.getTile() != null && item.getTile().equals(this)) {
            item.setTile(this);
        }

        items.add(item);
    }

    /**
     * Remove item from tile
     * @param item - the item to be removed
     * @throws ItemNotOnTileException - Thrown when item is not on this tile
     */
    public void remove(Item item) throws ItemNotOnTileException {
        if (!items.contains(item)) {
            throw new ItemNotOnTileException();
        }

        items.remove(item);
        item.setTile(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return x == tile.x && y == tile.y && Objects.equals(items, tile.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, items);
    }
}
