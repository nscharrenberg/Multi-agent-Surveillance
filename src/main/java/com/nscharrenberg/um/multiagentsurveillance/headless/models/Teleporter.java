package com.nscharrenberg.um.multiagentsurveillance.headless.models;

public class Teleporter extends Item {
    private Area<Tile> source;
    private Tile destination;
    private Angle direction;

    public Teleporter(Tile tile, Area<Tile> source, Tile destination, Angle direction) {
        super(tile);
        this.source = source;
        this.destination = destination;
        this.direction = direction;
    }

    public Area<Tile> getSource() {
        return source;
    }

    public void setSource(Area<Tile> source) {
        this.source = source;
    }

    public Tile getDestination() {
        return destination;
    }

    public void setDestination(Tile destination) {
        this.destination = destination;
    }

    public Angle getDirection() {
        return direction;
    }

    public void setDirection(Angle direction) {
        this.direction = direction;
    }
}
