package com.nscharrenberg.um.multiagentsurveillance.headless.models;

public enum ItemType {
    TILE(1, Tile.class),
    SHADOW(2, ShadowTile.class),
    WALL(3, Wall.class),
    SOUNDWAVE(4, SoundWave.class);

    ItemType(int order, Class<?> instance) {
        this.order = order;
        this.instance = instance;
    }

    private int order;
    private Class<?> instance;

    public int getOrder() {
        return order;
    }

    public Class<?> getInstance() {
        return instance;
    }

    public boolean isType(Item item) {
        // TODO: CHeck if item class is the same type as the instance

        return false;
    }
}