package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;

import java.util.HashMap;
import java.util.Map;

public class Frontier {
    private HashMap<Integer, HashMap<Integer, Tile>> frontier;
    private int unknownAreas = 0;
    private QueueNode queueNode;
    private int maxSize = 0;

    public Frontier() {
        this.frontier = new HashMap<>();
    }

    public Frontier(Tile tile) {
        this.frontier = new HashMap<>();
        this.frontier.put(tile.getX(), new HashMap<>());
        this.frontier.get(tile.getX()).put(tile.getY(), tile);
    }

    /**
     * Checks if a Tile is adjacent to an element in the frontier
     * @param tile - the tile to check
     * @return whether a Tile is Adjacent to the frontier
     */
    public boolean isAdjacent(Tile tile) {
        int up = tile.getY() + Angle.UP.getyIncrement();
        int down = tile.getY() + Angle.DOWN.getyIncrement();
        int right = tile.getX() + Angle.RIGHT.getxIncrement();
        int left = tile.getX() + Angle.LEFT.getxIncrement();

        if (frontier.containsKey(tile.getX())) {
            // Check if there is an adjacent tile up/down of current tile in the frontier
            return frontier.get(tile.getX()).containsKey(up) || frontier.get(tile.getX()).containsKey(down);
        } else if (frontier.containsKey(right)) {
            // Check if there is an adjacent tile right of the current tile in the frontier
            return frontier.get(right).containsKey(tile.getY());
        } else if (frontier.containsKey(left)) {
            // Check if there is an adjacent tile left of the current tile in the frontier
            return frontier.get(left).containsKey(tile.getY());
        }

        return false;
    }

    /**
     * Add tile to frontier if it's directly connected
     * @param tile - the tile to be added to the frontier
     * @return whether or not the tile was added to the frontier
     */
    public boolean add(Tile tile) {
        if (tile.isCollision()) {
            return false;
        }

        // reject if there is no adjacent tile in the frontier
        if (!isAdjacent(tile) && !frontier.isEmpty()) {
            return false;
        }

        if (!exceedsSize()) {
            return false;
        }

        // creates new row
        if (!frontier.containsKey(tile.getX())) {
            frontier.put(tile.getX(), new HashMap<>());
        }

        // stores it in the cell corresponding the the row/col
        frontier.get(tile.getX()).put(tile.getY(), tile);

        // accept
        return true;
    }

    private boolean exceedsSize() {
        if (frontier.size() > maxSize) {
            return true;
        }

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : frontier.entrySet()) {
            if (rowEntry.getValue().size() > maxSize) {
                return true;
            }
        }

        return false;
    }

    public HashMap<Integer, HashMap<Integer, Tile>> getFrontier() {
        return frontier;
    }

    public void setFrontier(HashMap<Integer, HashMap<Integer, Tile>> frontier) {
        this.frontier = frontier;
    }

    public void addUnknownArea() {
        this.unknownAreas++;
    }

    public int getUnknownAreas() {
        return unknownAreas;
    }

    public void setUnknownAreas(int unknownAreas) {
        this.unknownAreas = unknownAreas;
    }

    public QueueNode getQueueNode() {
        return queueNode;
    }

    public void setQueueNode(QueueNode queueNode) {
        if (this.queueNode != null && (queueNode.getDistance() <= this.queueNode.getDistance() || queueNode.getMoves().size() <= this.queueNode.getMoves().size())) {
            return;
        }

        this.queueNode = queueNode;
    }
}
