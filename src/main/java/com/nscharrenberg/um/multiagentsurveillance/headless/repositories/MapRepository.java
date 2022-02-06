package com.nscharrenberg.um.multiagentsurveillance.headless.repositories;

import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.TileArea;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MapRepository implements IMapRepository {
    private List<Tile> board;
    private TileArea targetArea;
    private TileArea guardSpawnArea;
    private TileArea intruderSpawnArea;

    public MapRepository() {
        this.board = new ArrayList<>();
    }

    public TileArea getBoardAsArea() {
        return new TileArea(board);
    }

    @Override
    public Tile findTileByCoordinates(int x, int y) throws BoardNotBuildException, InvalidTileException {
        boardInitCheck();

        Optional<Tile> found = this.board.stream().filter(tile -> tile.getX() == x && tile.getY() == y).findFirst();

        if (found.isEmpty()) {
            throw new InvalidTileException(x, y);
        }

        return found.get();
    }

    @Override
    public TileArea findTileAreaByBoundaries(int x1, int y1, int x2, int y2) throws BoardNotBuildException, InvalidTileException {
        boardInitCheck();

        TileArea boardArea = getBoardAsArea();

        List<Tile> foundArea = boardArea.subset(x1, y1, x2, y2);

        if (foundArea.isEmpty()) {
            throw new InvalidTileException(x1, y1, x2, y2);
        }

        return new TileArea(foundArea);
    }

    @Override
    public void addTargetArea(int x1, int y1, int x2, int y2) throws BoardNotBuildException, InvalidTileException {
        boardInitCheck();

        TileArea found = findTileAreaByBoundaries(x1, y1, x2, y2);

        if (found.isEmpty()) {
            throw new InvalidTileException(x1, y1, x2, y2);
        }

        targetArea = found;
    }

    @Override
    public void addGuardSpawnArea(int x1, int y1, int x2, int y2) throws BoardNotBuildException, InvalidTileException {
        boardInitCheck();

        TileArea found = findTileAreaByBoundaries(x1, y1, x2, y2);

        if (found.isEmpty()) {
            throw new InvalidTileException(x1, y1, x2, y2);
        }

        guardSpawnArea = found;
    }

    @Override
    public void addIntruderSpawnArea(int x1, int y1, int x2, int y2) throws BoardNotBuildException, InvalidTileException {
        boardInitCheck();

        TileArea found = findTileAreaByBoundaries(x1, y1, x2, y2);

        if (found.isEmpty()) {
            throw new InvalidTileException(x1, y1, x2, y2);
        }

        intruderSpawnArea = found;
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
    public TileArea getTargetArea() {
        return targetArea;
    }

    @Override
    public void setTargetArea(TileArea targetArea) {
        this.targetArea = targetArea;
    }

    @Override
    public TileArea getGuardSpawnArea() {
        return guardSpawnArea;
    }

    @Override
    public void setGuardSpawnArea(TileArea guardSpawnArea) {
        this.guardSpawnArea = guardSpawnArea;
    }

    @Override
    public TileArea getIntruderSpawnArea() {
        return intruderSpawnArea;
    }

    @Override
    public void setIntruderSpawnArea(TileArea intruderSpawnArea) {
        this.intruderSpawnArea = intruderSpawnArea;
    }

    private void boardInitCheck() throws BoardNotBuildException {
        if (board.isEmpty()) {
            throw new BoardNotBuildException();
        }
    }
}
