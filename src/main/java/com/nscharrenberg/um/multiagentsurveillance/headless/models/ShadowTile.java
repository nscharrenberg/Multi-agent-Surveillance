package com.nscharrenberg.um.multiagentsurveillance.headless.models;

import java.util.List;

public class ShadowTile extends Tile {
    public ShadowTile() {
    }

    public ShadowTile(List<Item> items) {
        super(items);
    }

    public ShadowTile(int x, int y, List<Item> items) {
        super(x, y, items);
    }

    public ShadowTile(int x, int y) {
        super(x, y);
    }
}