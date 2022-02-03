package com.nscharrenberg.um.multiagentsurveillance.headless.repositories;

import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Area;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;

import java.util.ArrayList;
import java.util.List;

public class MapRepository implements IMapRepository {
    private List<Tile> board;
    private Area<Item> targetArea;
    private Area<Item> guardSpawnArea;
    private Area<Item> intruderSpawnArea;

    public MapRepository() {
        this.board = new ArrayList<>();
    }

    @Override
    public List<Tile> getBoard() {
        return board;
    }

    @Override
    public void setBoard(List<Tile> board) {
        this.board = board;
    }

    @Override
    public Area<Item> getTargetArea() {
        return targetArea;
    }

    @Override
    public void setTargetArea(Area<Item> targetArea) {
        this.targetArea = targetArea;
    }

    @Override
    public Area<Item> getGuardSpawnArea() {
        return guardSpawnArea;
    }

    @Override
    public void setGuardSpawnArea(Area<Item> guardSpawnArea) {
        this.guardSpawnArea = guardSpawnArea;
    }

    @Override
    public Area<Item> getIntruderSpawnArea() {
        return intruderSpawnArea;
    }

    @Override
    public void setIntruderSpawnArea(Area<Item> intruderSpawnArea) {
        this.intruderSpawnArea = intruderSpawnArea;
    }
}
