package com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Area;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;

import java.util.List;

public interface IMapRepository {
    List<Tile> getBoard();

    void setBoard(List<Tile> board);

    Area<Item> getTargetArea();

    void setTargetArea(Area<Item> targetArea);

    Area<Item> getGuardSpawnArea();

    void setGuardSpawnArea(Area<Item> guardSpawnArea);

    Area<Item> getIntruderSpawnArea();

    void setIntruderSpawnArea(Area<Item> intruderSpawnArea);
}
