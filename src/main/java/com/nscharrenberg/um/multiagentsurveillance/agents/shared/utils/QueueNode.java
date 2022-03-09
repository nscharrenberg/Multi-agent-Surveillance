package com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;

import java.util.LinkedList;
import java.util.Queue;

public class QueueNode {
    Tile tile;
    Angle entrancePosition;
    Queue<Angle> moves;

    public QueueNode(Tile tile, Angle entrancePosition) {
        this.tile = tile;
        this.moves = new LinkedList<>();
        this.entrancePosition = entrancePosition;
    }

    public QueueNode(Tile tile, Angle entrancePosition, Queue<Angle> moves) {
        this.tile = tile;
        this.moves = moves;
        this.entrancePosition = entrancePosition;
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

    public Queue<Angle> getMoves() {
        return moves;
    }

    public void setMoves(Queue<Angle> moves) {
        this.moves = moves;
    }

    public Angle getEntrancePosition() {
        return entrancePosition;
    }

    public void setEntrancePosition(Angle entrancePosition) {
        this.entrancePosition = entrancePosition;
    }
}
