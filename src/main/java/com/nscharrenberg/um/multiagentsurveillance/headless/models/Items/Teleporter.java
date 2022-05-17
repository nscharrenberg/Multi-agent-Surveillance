package com.nscharrenberg.um.multiagentsurveillance.headless.models.Items;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;

public class Teleporter extends Item {
    private TileArea source;
    private Action direction;

    public Teleporter(TileArea source, Tile destination, Action direction) {
        super(destination);
        this.source = source;
        this.direction = direction;
    }

    public TileArea getSource() {
        return source;
    }

    public void setSource(TileArea source) {
        this.source = source;
    }

    public Action getDirection() {
        return direction;
    }

    public void setDirection(Action direction) {
        this.direction = direction;
    }
}
