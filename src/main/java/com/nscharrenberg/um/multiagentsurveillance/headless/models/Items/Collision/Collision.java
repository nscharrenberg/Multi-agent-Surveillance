package com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

public abstract class Collision extends Item {
    public Collision(Tile tile) {
        super(tile);
    }
}
