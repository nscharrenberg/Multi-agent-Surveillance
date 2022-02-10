package com.nscharrenberg.um.multiagentsurveillance.headless.models;

import java.util.*;

public class TileArea extends Area<Tile> {

    public TileArea() {
        super();
    }

    public TileArea(HashMap<Integer, HashMap<Integer, Tile>> region) {
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
    public HashMap<Integer, HashMap<Integer, Tile>> subset(int x1, int y1, int x2, int y2) {
        HashMap<Integer, HashMap<Integer, Tile>> generatedSubset = new HashMap<>();

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : region.entrySet()) {
            if (rowEntry.getKey() >= x1 && rowEntry.getKey() <= x2) {
                for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                    if (colEntry.getKey() >= y1 && colEntry.getKey() <= y2) {

                        if (!generatedSubset.containsKey(rowEntry.getKey())) {
                            generatedSubset.put(rowEntry.getKey(), new HashMap<>());
                        }

                        generatedSubset.get(rowEntry.getKey()).put(colEntry.getKey(), colEntry.getValue());
                    }
                }
            }
        }

        if (generatedSubset.isEmpty()) {
            return new HashMap<>();
        }

        return generatedSubset;
    }

    @Override
    public Optional<Tile> getByCoordinates(int x, int y) {
        return Optional.of(region.get(x).get(y));
    }

    @Override
    public List<Tile> getBounds() {
        int left = 0;
        int right = region.size();
        int top = 0;
        int bottom = region.get(0).size();

       ArrayList<Tile> generatedSubset = new ArrayList<>();
       generatedSubset.add(region.get(left).get(top));
       generatedSubset.add(region.get(left).get(bottom));
       generatedSubset.add(region.get(right).get(top));
       generatedSubset.add(region.get(right).get(bottom));

        return generatedSubset;
    }
}
