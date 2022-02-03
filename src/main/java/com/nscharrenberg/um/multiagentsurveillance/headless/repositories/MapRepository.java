package com.nscharrenberg.um.multiagentsurveillance.headless.repositories;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Area;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;

import java.util.ArrayList;
import java.util.List;

public class MapRepository {
    // All Tiles
    private List<Tile> board;

    // Collection of all items
    private Area<Item> targetArea;
    private Area<Item> guardSpawnArea;
    private Area<Item> intruderSpawnArea;

    public MapRepository() {
        this.board = new ArrayList<>();
    }

    public List<Tile> getBoard() {
        return board;
    }

    public void setBoard(List<Tile> board) {
        this.board = board;
    }

    public Area<Item> getTargetArea() {
        return targetArea;
    }

    public void setTargetArea(Area<Item> targetArea) {
        this.targetArea = targetArea;
    }

    public Area<Item> getGuardSpawnArea() {
        return guardSpawnArea;
    }

    public void setGuardSpawnArea(Area<Item> guardSpawnArea) {
        this.guardSpawnArea = guardSpawnArea;
    }

    public Area<Item> getIntruderSpawnArea() {
        return intruderSpawnArea;
    }

    public void setIntruderSpawnArea(Area<Item> intruderSpawnArea) {
        this.intruderSpawnArea = intruderSpawnArea;
    }
}
