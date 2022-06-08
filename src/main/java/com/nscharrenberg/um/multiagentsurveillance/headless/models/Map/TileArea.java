package com.nscharrenberg.um.multiagentsurveillance.headless.models.Map;

import com.rits.cloning.Cloner;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

public class TileArea extends Area<Tile> {

    public TileArea() {
        super();
    }

    public TileArea(HashMap<Integer, HashMap<Integer, Tile>> region) {
        super(region);
    }

    public TileArea(List<Tile> tiles) {
        super();

        for (Tile tile : tiles) {
            if (!region.containsKey(tile.getX())) {
                region.put(tile.getX(), new HashMap<>());
            }

            region.get(tile.getX()).put(tile.getY(), tile);
        }
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
        if (region.get(x) == null || region.get(x).isEmpty()) {
            return Optional.empty();
        }

        if (region.get(x).get(y) == null) {
            return Optional.empty();
        }

        return Optional.of(region.get(x).get(y));
    }

    @Override
    public List<Tile> getBounds() {
        Integer left = null;
        Integer right = null;
        Integer top = null;
        Integer bottom = null;

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : region.entrySet()) {
            if (left == null || rowEntry.getKey() < left) {
                left = rowEntry.getKey();
            }

            if (right == null || rowEntry.getKey() > right) {
                right = rowEntry.getKey();
            }

            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                if (top == null || colEntry.getKey() < top) {
                    top = colEntry.getKey();
                }

                if (bottom == null || colEntry.getKey() > bottom) {
                    bottom = colEntry.getKey();
                }
            }
        }

       ArrayList<Tile> generatedSubset = new ArrayList<>();
       generatedSubset.add(region.get(left).get(top));
       generatedSubset.add(region.get(left).get(bottom));
       generatedSubset.add(region.get(right).get(top));
       generatedSubset.add(region.get(right).get(bottom));

        return generatedSubset;
    }

    @Override
    public int width() {
        return this.region.size();
    }

    @Override
    public int height() {
        Optional<Map.Entry<Integer, HashMap<Integer, Tile>>> found = this.getRegion().entrySet().stream().findFirst();

        if (this.region.size() <= 0 || found.isEmpty()) {
            return 0;
        }

        return this.region.get(found.get().getKey()).size();
    }

    @Override
    public Map.Entry<Map.Entry<Integer, Integer>, Map.Entry<Integer, Integer>> bounds() {
        Integer lowerBoundRow = null;
        Integer upperBoundRow = null;
        Integer upperBoundCol = null;
        Integer lowerBoundCol = null;

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : region.entrySet()) {
            if (lowerBoundRow == null || rowEntry.getKey() < lowerBoundRow) {
                lowerBoundRow = rowEntry.getKey();
            }

            if (upperBoundRow == null || rowEntry.getKey() > upperBoundRow) {
                upperBoundRow = rowEntry.getKey();
            }

            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                if (lowerBoundCol == null || colEntry.getKey() < lowerBoundCol) {
                    lowerBoundCol = colEntry.getKey();
                }

                if (upperBoundCol == null || colEntry.getKey() > upperBoundCol) {
                    upperBoundCol = colEntry.getKey();
                }
            }
        }

        return new AbstractMap.SimpleEntry<>(
                new AbstractMap.SimpleEntry<>(lowerBoundRow, upperBoundRow),
                new AbstractMap.SimpleEntry<>(lowerBoundCol, upperBoundCol)
        );
    }

    @Override
    public HashMap<Integer, Tile> getRow(int row) {
        return this.region.get(row);
    }

    @Override
    public HashMap<Integer, Tile> getCol(int col) {
        HashMap<Integer, Tile> result = new HashMap<>();

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : this.region.entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                if (colEntry.getKey() == col) {
                    result.put(rowEntry.getKey(), colEntry.getValue());
                }
            }
        }

        return result;
    }

    @Override
    public void add(boolean overwrite, Tile... tiles) {
        for (Tile tile : tiles) {
            add(tile, overwrite);
        }
    }

    @Override
    public void add(Tile... tiles) {
        for (Tile tile : tiles) {
            add(tile);
        }
    }

    @Override
    public void add(List<Tile> tiles) {
        for (Tile tile : tiles) {
            add(tile);
        }
    }

    @Override
    public void add(Tile tile, boolean overwrite) {
        Optional<Tile> existing = getByCoordinates(tile.getX(), tile.getY());

        if (existing.isEmpty() && !region.containsKey(tile.getX())) {
            region.put(tile.getX(), new HashMap<>());
        }

        if (existing.isPresent() && !overwrite) {
            return;
        }

        if(existing.isEmpty())
            size++;

        region.get(tile.getX()).put(tile.getY(), tile);
    }

    @Override
    public void add(Tile tile) {
        add(tile, true);
    }

    @Override
    public Area<Tile> merge(Area<Tile> target) throws ConcurrentModificationException {
        Cloner cloner = new Cloner();
        cloner.deepCloneDontCloneInstances(SecureRandom.class);
        cloner.deepCloneDontCloneInstances(LinkedList.class);
        cloner.deepCloneDontCloneInstances(List.class);
        cloner.deepCloneDontCloneInstances(ArrayList.class);
        cloner.deepCloneDontCloneInstances(PriorityQueue.class);
        cloner.dontCloneInstanceOf(SecureRandom.class);
        cloner.dontCloneInstanceOf(LinkedList.class);
        cloner.dontCloneInstanceOf(List.class);
        cloner.dontCloneInstanceOf(ArrayList.class);
        cloner.dontCloneInstanceOf(PriorityQueue.class);
        cloner.dontCloneInstanceOf(Instant.class);
        HashMap<Integer, HashMap<Integer, Tile>> cloned = cloner.deepClone(region);

        TileArea newArea = new TileArea(cloned);

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : target.getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                newArea.add(colEntry.getValue(), false);
            }
        }

        return newArea;
    }
}
