package com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Collision;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

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
