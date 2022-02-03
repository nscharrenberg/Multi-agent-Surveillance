package com.nscharrenberg.um.multiagentsurveillance.headless.models;

public class Item {
    private Area<Tile> area;

    public Item(Area<Tile> area) {
        this.area = area;
    }

    public Area<Tile> getArea() {
        return area;
    }

    public void setArea(Area<Tile> area) {
        this.area = area;
    }
}
