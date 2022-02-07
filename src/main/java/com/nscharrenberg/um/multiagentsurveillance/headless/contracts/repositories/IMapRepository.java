package com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories;

import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;

import java.util.List;

public interface IMapRepository {
    TileArea getBoardAsArea();

    Tile findTileByCoordinates(int x, int y) throws BoardNotBuildException, InvalidTileException;

    TileArea findTileAreaByBoundaries(int x1, int y1, int x2, int y2) throws BoardNotBuildException, InvalidTileException;

    void addTargetArea(int x1, int y1, int x2, int y2) throws BoardNotBuildException, InvalidTileException;

    void addTeleporter(int x1, int y1, int x2, int y2, int destX, int destY, Angle direction) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException;

    void addWall(int x1, int y1, int x2, int y2) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException;

    void addWall(int x, int y) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException;

    void addGuardSpawnArea(int x1, int y1, int x2, int y2) throws BoardNotBuildException, InvalidTileException;

    void addIntruderSpawnArea(int x1, int y1, int x2, int y2) throws BoardNotBuildException, InvalidTileException;

    List<Tile> getBoard();

    void setBoard(List<Tile> board);

    TileArea getTargetArea();

    void setTargetArea(TileArea targetArea);

    TileArea getGuardSpawnArea();

    void setGuardSpawnArea(TileArea guardSpawnArea);

    TileArea getIntruderSpawnArea();

    void setIntruderSpawnArea(TileArea intruderSpawnArea);
}
