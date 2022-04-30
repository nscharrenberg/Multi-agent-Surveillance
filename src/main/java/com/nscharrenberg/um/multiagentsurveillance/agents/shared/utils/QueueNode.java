package com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;

import java.util.LinkedList;
import java.util.Queue;

public class QueueNode {
    int pathCost;
    Tile tile;
    Action entrancePosition;
    Queue<Action> moves;

    public QueueNode(Tile tile, Action entrancePosition) {
        this.tile = tile;
        this.moves = new LinkedList<>();
        this.entrancePosition = entrancePosition;
    }

    public QueueNode(Tile tile, Action entrancePosition, Queue<Action> moves, int pathCost) {
        this.tile = tile;
        this.moves = moves;
        this.entrancePosition = entrancePosition;
        this.pathCost = pathCost;
    }

    public int getPathCost() {
        return pathCost;
    }

    public void setPathCost(int pathCost) {
        this.pathCost = pathCost;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public int getDistance() {
        return moves.size();
    }

    public Queue<Action> getMoves() {
        return moves;
    }

    public void setMoves(Queue<Action> moves) {
        this.moves = moves;
    }

    public Action getEntrancePosition() {
        return entrancePosition;
    }

    public void setEntrancePosition(Action entrancePosition) {
        this.entrancePosition = entrancePosition;
    }
}
