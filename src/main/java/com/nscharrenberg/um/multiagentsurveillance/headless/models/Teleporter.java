package com.nscharrenberg.um.multiagentsurveillance.headless.models;

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
