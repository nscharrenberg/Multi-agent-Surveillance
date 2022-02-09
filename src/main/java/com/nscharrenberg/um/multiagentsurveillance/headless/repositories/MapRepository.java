package com.nscharrenberg.um.multiagentsurveillance.headless.repositories;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;

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

    @Override
    public void move(Player player, Angle direction) throws CollisionException, InvalidTileException, ItemNotOnTileException, ItemAlreadyOnTileException {
        Angle currentDirection = player.getDirection();

        // Rotate the player when it's not facing the same direction as it wants to go to.
        if (!currentDirection.equals(direction)) {
            player.setDirection(direction);
            return;
        }

        Tile currentPosition = player.getTile();

        Optional<Item> collisionFound = currentPosition.getItems().stream().filter(item -> item instanceof Collision && item != player).findFirst();

        if (collisionFound.isPresent()) {
            throw new CollisionException();
        }

        // find new tile
        int nextX = player.getTile().getX() + direction.getxIncrement();
        int nextY = player.getTile().getY() + direction.getyIncrement();
        Optional<Tile> nextPositionOpt = Factory.getMapRepository().getBoardAsArea().getByCoordinates(nextX, nextY);

        if (nextPositionOpt.isEmpty()) {
            throw new InvalidTileException(nextX, nextY);
        }

        Tile nextPosition = nextPositionOpt.get();

        // remove player from tile
        currentPosition.remove(player);

        // add player to tile
        nextPosition.add(player);
    }

    @Override
    public TileArea getBoardAsArea() {
        return new TileArea(board);
    }

    @Override
    public void buildEmptyBoard() throws IllegalArgumentException {
        buildEmptyBoard(Factory.getGameRepository().getWidth(), Factory.getGameRepository().getHeight());
    }

    @Override
    public void buildEmptyBoard(int width, int height) throws IllegalArgumentException {
        if (width == 0 || height == 0) {
            throw new IllegalArgumentException("Unable to build a board with a width or height of 0");
        }

        for (int i = 0; i <= width; i++) {
            for (int j = 0; j <= height; j++) {
                Factory.getMapRepository().getBoard().add(new Tile(i, j));
            }
        }
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
    public void addTeleporter(int x1, int y1, int x2, int y2, int destX, int destY, Angle direction) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException {
        boardInitCheck();

        TileArea found = findTileAreaByBoundaries(x1, y1, x2, y2);
        Tile destination = findTileByCoordinates(destX, destY);

        Teleporter teleporter = new Teleporter(found, destination, direction);
        destination.add(teleporter);

        for (Tile tile : found.getRegion()) {
            tile.add(teleporter);
        }
    }

    @Override
    public void addShaded(int x1, int y1, int x2, int y2) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException {
        boardInitCheck();

        TileArea area = findTileAreaByBoundaries(x1, y1, x2, y2);

        for (Tile tile : area.getRegion()) {
            addShaded(tile.getX(), tile.getY());
        }
    }

    @Override
    public void addShaded(int x1, int y1) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException {
        boardInitCheck();

        Tile found = findTileByCoordinates(x1, y1);

        if (found instanceof ShadowTile) {
            throw new ItemAlreadyOnTileException();
        }

        int index = Factory.getMapRepository().getBoard().indexOf(found);

        ShadowTile shadedTile = new ShadowTile(found.getX(), found.getY(), found.getItems());

        for (Item item : shadedTile.getItems()) {
            item.setTile(shadedTile);
        }

        Factory.getMapRepository().getBoard().set(index, shadedTile);
    }

    @Override
    public void addWall(int x1, int y1, int x2, int y2) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException {
        boardInitCheck();

        TileArea area = findTileAreaByBoundaries(x1, y1, x2, y2);

        for (Tile tile : area.getRegion()) {
            Wall wall = new Wall(tile);
            tile.add(wall);
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
