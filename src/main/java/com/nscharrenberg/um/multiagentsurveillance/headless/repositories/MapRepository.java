package com.nscharrenberg.um.multiagentsurveillance.headless.repositories;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;

import java.util.*;

public class MapRepository implements IMapRepository {
    private IGameRepository gameRepository;
    private IPlayerRepository playerRepository;

    private TileArea board;
    private TileArea targetArea;
    private TileArea guardSpawnArea;
    private TileArea intruderSpawnArea;

    private HashMap<Integer, Marker> placed_markers;
    private int hashmapCounter;

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
    public void addTeleporter(int x1, int y1, int x2, int y2, int destX, int destY, Action direction) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException {
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
    public void addMarker(Marker.MarkerType type, int x1, int y1, Player player) throws BoardNotBuildException, InvalidTileException, ItemAlreadyOnTileException {
        boardInitCheck();

        Tile found = findTileByCoordinates(x1, y1);
        Marker marker = new Marker(type, found, player);

        Tile[] neighboringTiles = calculateNeigboringTiles(marker);
        for (int i = 0; i < neighboringTiles.length; i++) {
            neighboringTiles[i].add(marker);
            placed_markers.put(hashmapCounter, marker);
            hashmapCounter++;
        }
    }

    @Override
    public Tile[] calculateNeigboringTiles(Marker marker) throws InvalidTileException, BoardNotBuildException {
        int distance = marker.RANGE;
        int current_x = marker.getTile().getX();
        int current_y = marker.getTile().getX();
        int top_left_x = current_x - distance;
        int top_left_y = current_y - distance;
        int top_right_x = current_x + distance;
        int bottom_left_y = current_y + distance;
        int manhattanDistance;
        Tile[] listOfTiles = new Tile[]{};
        int k = 0;

        for (int i = top_left_x; i > top_right_x; i++) {
            for (int j = top_left_y; j > bottom_left_y; j++) {
                manhattanDistance = Math.abs(current_x - i) + Math.abs(current_y - j);
                if (manhattanDistance <= distance) {
                    listOfTiles[k] = findTileByCoordinates(i, j);;
                    k++;
                }
            }
        }
        return listOfTiles;
    }

    @Override
    public void removeMarker(Marker marker) throws BoardNotBuildException, InvalidTileException, ItemNotOnTileException {
        boardInitCheck();

        int x_position = marker.getTile().getX();
        int y_position = marker.getTile().getY();

        Tile found = findTileByCoordinates(x_position, y_position);
        found.remove(marker);
    }

    @Override
    public void checkMarkers() throws BoardNotBuildException, InvalidTileException, ItemNotOnTileException {
        boardInitCheck();

        for (Map.Entry<Integer, Marker> entry : placed_markers.entrySet()) {
            if (entry.getValue().getCurrentDuration() == 0) {
                removeMarker(entry.getValue());
                placed_markers.remove(entry);
            }
            entry.getValue().decrementCurrentDuration();
        }
    }

    @Override
    public HashMap<Integer, Marker> getListOfPlacedMarkers() {
        return placed_markers;
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
