package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;

import java.util.LinkedList;
import java.util.Queue;

public class QueueNode {
    Tile tile;
    Queue<Tile> tiles;
    int distance;

    public QueueNode(Tile tile, int distance) {
        this.tile = tile;
        this.distance = distance;
        this.tiles = new LinkedList<>();
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Queue<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(Queue<Tile> tiles) {
        this.tiles = tiles;
    }
}
