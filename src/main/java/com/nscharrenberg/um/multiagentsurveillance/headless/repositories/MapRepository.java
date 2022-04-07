package com.nscharrenberg.um.multiagentsurveillance.headless.repositories;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Teleporter;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Wall;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.ShadowTile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MapRepository implements IMapRepository {
    private IGameRepository gameRepository;
    private IPlayerRepository playerRepository;

    private TileArea board;
    private TileArea targetArea;
    private TileArea guardSpawnArea;
    private TileArea intruderSpawnArea;

    public MapRepository(IGameRepository gameRepository, IPlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;

        this.board = new TileArea();
    }

    public MapRepository() {
        this.playerRepository = Factory.getPlayerRepository();
        this.gameRepository = Factory.getGameRepository();

        this.board = new TileArea();
    }

    @Override
    public TileArea getBoardAsArea() {
        return this.board;
    }

    @Override
    public void buildEmptyBoard() throws IllegalArgumentException {
        buildEmptyBoard(gameRepository.getWidth(), gameRepository.getHeight());
    }

    @Override
    public void buildEmptyBoard(int width, int height) throws IllegalArgumentException {
        if (width == 0 || height == 0) {
            throw new IllegalArgumentException("Unable to build a board with a width or height of 0");
        }

        for (int i = 0; i <= width; i++) {
            for (int j = 0; j <= height; j++) {
                if (!getBoardAsArea().getRegion().containsKey(i)) {
                    getBoard().getRegion().put(i, new HashMap<>());
                }

                getBoard().getRegion().get(i).put(j, new Tile(i, j));
            }
        }
    }

    @Override
    public Tile findTileByCoordinates(int x, int y) throws BoardNotBuildException, InvalidTileException {
        boardInitCheck();

        Optional<Tile> found = this.board.getByCoordinates(x, y);

        if (found.isEmpty()) {
            throw new InvalidTileException(x, y);
        }

        return found.get();
    }

    @Override
    public TileArea findTileAreaByBoundaries(int x1, int y1, int x2, int y2) throws BoardNotBuildException, InvalidTileException {
        boardInitCheck();

        HashMap<Integer, HashMap<Integer, Tile>> foundArea = board.subset(x1, y1, x2, y2);

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
    public void addTeleporter(int x1, int y1, int x2, int y2, int destX, int destY, Angle direction) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException {
        boardInitCheck();

        TileArea found = findTileAreaByBoundaries(x1, y1, x2, y2);
        Tile destination = findTileByCoordinates(destX, destY);

        Teleporter teleporter = new Teleporter(found, destination, direction);
        destination.add(teleporter);

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : found.getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                colEntry.getValue().add(teleporter);
            }
        }

        teleporter.setSource(found);
    }

    @Override
    public void addShaded(int x1, int y1, int x2, int y2) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException {
        boardInitCheck();

        TileArea area = findTileAreaByBoundaries(x1, y1, x2, y2);

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : area.getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                addShaded(rowEntry.getKey(), colEntry.getKey());
            }
        }
    }

    @Override
    public void addShaded(int x1, int y1) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException {
        boardInitCheck();

        Tile found = findTileByCoordinates(x1, y1);

        if (found instanceof ShadowTile) {
            throw new ItemAlreadyOnTileException();
        }

        ShadowTile shadedTile = new ShadowTile(found.getX(), found.getY(), found.getItems());

        for (Item item : shadedTile.getItems()) {
            item.setTile(shadedTile);
        }

        getBoard().getRegion().get(x1).put(y1, shadedTile);
    }

    @Override
    public void addWall(int x1, int y1, int x2, int y2) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException {
        boardInitCheck();

        TileArea area = findTileAreaByBoundaries(x1, y1, x2, y2);

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : area.getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                Wall wall = new Wall(colEntry.getValue());
                colEntry.getValue().add(wall);
            }
        }
    }

    @Override
    public void addWall(int x, int y) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException {
        boardInitCheck();

        Tile found = findTileByCoordinates(x, y);

        Wall wall = new Wall(found);
        found.add(wall);
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
    public TileArea getBoard() {
        return board;
    }

    @Override
    public void setBoard(TileArea board) {
        this.board = board;
    }

    @Override
    public void setBoard(HashMap<Integer, HashMap<Integer, Tile>> board) {
        this.board = new TileArea(board);
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

    @Override
    public IGameRepository getGameRepository() {
        return gameRepository;
    }

    @Override
    public void setGameRepository(IGameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public IPlayerRepository getPlayerRepository() {
        return playerRepository;
    }

    @Override
    public void setPlayerRepository(IPlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
}
