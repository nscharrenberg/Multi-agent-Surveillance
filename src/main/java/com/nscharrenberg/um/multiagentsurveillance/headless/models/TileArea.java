package com.nscharrenberg.um.multiagentsurveillance.headless.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TileArea extends Area<Tile> {

    public TileArea() {
        super();
    }

    public TileArea(List<Tile> region) {
        super(region);
    }

    @Override
    public boolean within(int x1, int y1, int x2, int y2) {
        return !subset(x1, y1, x2, y2).isEmpty();
    }

    @Override
    public boolean within(int x, int y) {
        return getByCoordinates(x, y).isPresent();
    }

    @Override
    public List<Tile> subset(int x1, int y1, int x2, int y2) {
        List<Tile> subset = region.stream().filter(tile -> tile.getX() >= x1 && tile.getX() <= x2 && tile.getY() >= y1 && tile.getY() <= y2).toList();

        if (subset.isEmpty()) {
            return new ArrayList<>();
        }

        return subset;
    }

    @Override
    public Optional<Tile> getByCoordinates(int x, int y) {
        return region.stream().filter(tile -> tile.getX() == x && tile.getY() == y).findFirst();
    }

    @Override
    public List<Tile> getBounds() {
        Optional<Tile> leftBound = region.stream().min(Comparator.comparing(Tile::getX));
        Optional<Tile> rightBound = region.stream().max(Comparator.comparing(Tile::getX));
        Optional<Tile> topBound = region.stream().min(Comparator.comparing(Tile::getY));
        Optional<Tile> bottomBound = region.stream().max(Comparator.comparing(Tile::getY));

        if (leftBound.isEmpty() || rightBound.isEmpty() || topBound.isEmpty() || bottomBound.isEmpty()) {
            return new ArrayList<>();
        }

        if (leftBound.get().getX() == rightBound.get().getX()) {
            List<Tile> boundRegion = new ArrayList<>();
            boundRegion.add(topBound.get());
            boundRegion.add(bottomBound.get());

            return boundRegion;
        }

        if (topBound.get().getY() == bottomBound.get().getY()) {
            List<Tile> boundRegion = new ArrayList<>();
            boundRegion.add(leftBound.get());
            boundRegion.add(rightBound.get());

            return boundRegion;
        }

        Optional<Tile> topLeft = region.stream().filter(tile -> tile.getX() == leftBound.get().getX() && tile.getY() == topBound.get().getY()).findFirst();
        Optional<Tile> topRight = region.stream().filter(tile -> tile.getX() == rightBound.get().getX() && tile.getY() == topBound.get().getY()).findFirst();
        Optional<Tile> bottomLeft = region.stream().filter(tile -> tile.getX() == leftBound.get().getX() && tile.getY() == bottomBound.get().getY()).findFirst();
        Optional<Tile> bottomRight = region.stream().filter(tile -> tile.getX() == rightBound.get().getX() && tile.getY() == bottomBound.get().getY()).findFirst();

        if (topLeft.isEmpty() || topRight.isEmpty() || bottomLeft.isEmpty() || bottomRight.isEmpty()) {
            return new ArrayList<>();
        }

        List<Tile> boundRegion = new ArrayList<>();
        boundRegion.add(topLeft.get());
        boundRegion.add(bottomLeft.get());
        boundRegion.add(topRight.get());
        boundRegion.add(bottomRight.get());

        return boundRegion;
    }
}
