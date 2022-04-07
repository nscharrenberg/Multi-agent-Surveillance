package com.nscharrenberg.um.multiagentsurveillance.headless.models.Items;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;

public class Teleporter extends Item {
    private TileArea source;
    private Angle direction;

    public Teleporter(TileArea source, Tile destination, Angle direction) {
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

    public Angle getDirection() {
        return direction;
    }

    public void setDirection(Angle direction) {
        this.direction = direction;
    }
}
