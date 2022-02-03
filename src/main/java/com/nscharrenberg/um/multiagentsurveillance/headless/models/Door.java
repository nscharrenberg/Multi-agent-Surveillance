package com.nscharrenberg.um.multiagentsurveillance.headless.models;

public class Door extends Collision {
    private boolean isOpen;

    public Door(Tile tile) {
        super(tile);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
