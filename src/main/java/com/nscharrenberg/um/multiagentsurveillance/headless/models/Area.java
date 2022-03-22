package com.nscharrenberg.um.multiagentsurveillance.headless.models;

import java.util.*;

// Collection of objects
public abstract class Area<T> {
    protected HashMap<Integer, HashMap<Integer, T>> region;

    public Area() {
        this.region = new HashMap<>();
    }

    public Area(HashMap<Integer, HashMap<Integer, T>> region) {
        this.region = region;
    }

    public HashMap<Integer, HashMap<Integer, T>> getRegion() {
        return region;
    }

    public void setRegion(HashMap<Integer, HashMap<Integer, T>> region) {
        this.region = region;
    }

    public boolean isEmpty() {
        return region.isEmpty();
    }

    public abstract boolean within(int x1, int y1, int x2, int y2);

    public abstract boolean within(int x, int y);

    public abstract HashMap<Integer, HashMap<Integer, T>> subset(int x1, int y1, int x2, int y2);

    public abstract Optional<T> getByCoordinates(int x, int y);

    public abstract List<T> getBounds();

    public abstract int width();

    public abstract int height();

    public abstract Map.Entry<Map.Entry<Integer, Integer>, Map.Entry<Integer, Integer>> bounds();

    public abstract HashMap<Integer, Tile> getRow(int col);

    public abstract HashMap<Integer, Tile> getCol(int col);

    public abstract void add(Tile tile);

    public abstract void add(boolean overwrite, Tile... tiles);

    public abstract void add(Tile... tiles);

    public abstract void add(List<Tile> tiles);

    public abstract void add(Tile tile, boolean overwrite);

    public abstract Area<Tile> merge(Area<Tile> target);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Area<?> area = (Area<?>) o;
        return Objects.equals(region, area.region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(region);
    }
}
