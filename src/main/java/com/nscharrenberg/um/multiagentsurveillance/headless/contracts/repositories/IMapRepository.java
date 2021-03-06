package com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories;

import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Marker;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.MarkerSmell;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;

import java.util.ArrayList;
import java.util.HashMap;

public interface IMapRepository {
    /**
     * Convert the board to a TileArea
     * @return TileArea representation of the board
     */
    TileArea getBoardAsArea();

    /**
     * Builds an empty board with tiles based on the GameRepositories provided width and height
     * @throws IllegalArgumentException - Thrown when width or height is 0
     */
    void buildEmptyBoard() throws IllegalArgumentException;

    /**
     * Builds an empty board with tiles based on the given width and height
     * @param width - The given width
     * @param height - the given height
     * @throws IllegalArgumentException - Thrown when width or height is 0
     */
    void buildEmptyBoard(int width, int height) throws IllegalArgumentException;

    /**
     * Finds the tile on the board by its coordinates
     * @param x - the x coordinate
     * @param y - the y coordinate
     * @return - the Tile that was found
     * @throws BoardNotBuildException - Thrown when the board has not been initialized (no tiles exist)
     * @throws InvalidTileException - Thrown when the tile could not be found and is therefore invalid (out-of-bound)
     */
    Tile findTileByCoordinates(int x, int y) throws BoardNotBuildException, InvalidTileException;

    /**
     * Find a group of tiles on the board by its boundaries.
     * @param x1 - left bound
     * @param y1 - top bound
     * @param x2 - right bound
     * @param y2 - lower bound
     * @return A TileArea of all the tiles within the given boundary
     * @throws BoardNotBuildException - Thrown when the board has not been initialized (no tiles exist)
     * @throws InvalidTileException - Thrown when there is at least one tile that is not within the board.
     */
    TileArea findTileAreaByBoundaries(int x1, int y1, int x2, int y2) throws BoardNotBuildException, InvalidTileException;

    /**
     * Assign an area as target area
     * @param x1 - left bound
     * @param y1 - top bound
     * @param x2 - right bound
     * @param y2 - lower bound
     * @throws BoardNotBuildException - Thrown when the board has not been initialized (no tiles exist)
     * @throws InvalidTileException - Thrown when there is at least one tile that is not within the board.
     */
    void addTargetArea(int x1, int y1, int x2, int y2) throws BoardNotBuildException, InvalidTileException;
    void addTargetArea(int x1, int y1) throws BoardNotBuildException, InvalidTileException;

    /**
     * Assigns an area for the teleport source and a tile for its destination
     * @param x1 - left source bound
     * @param y1 - top source bound
     * @param x2 - right source bound
     * @param y2 - bottom source bound
     * @param destX - destination X
     * @param destY - destination Y
     * @param direction - The direction the player comes out of the teleport
     * @throws BoardNotBuildException - Thrown when the board has not been initialized (no tiles exist)
     * @throws InvalidTileException - Thrown when there is at least one tile that is not within the board.
     * @throws ItemAlreadyOnTileException - Thrown when the item is already present on the given tile.
     */
    void addTeleporter(int x1, int y1, int x2, int y2, int destX, int destY, Action direction) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException;
    void addTeleporter(int x1, int y1, int destX, int destY, Action direction) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException;

    /**
     * Turns a group of Tiles into shaded tiles
     * @param x1 - left bound
     * @param y1 - top bound
     * @param x2 - right bound
     * @param y2 - lower bound
     * @throws BoardNotBuildException - Thrown when the board has not been initialized (no tiles exist)
     * @throws InvalidTileException - Thrown when there is at least one tile that is not within the board.
     * @throws ItemAlreadyOnTileException - Thrown when the item is already present on the given tile.
     */
    void addShaded(int x1, int y1, int x2, int y2) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException;

    /**
     * Turns a tile into a shaded tile
     * @param x1 - x coordinate
     * @param y1 - y coordinate
     * @throws BoardNotBuildException - Thrown when the board has not been initialized (no tiles exist)
     * @throws InvalidTileException - Thrown when there is at least one tile that is not within the board.
     * @throws ItemAlreadyOnTileException - Thrown when the item is already present on the given tile.
     */
    void addShaded(int x1, int y1) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException;

    /**
     * Adds walls to a given area
     * @param x1 - left source bound
     * @param y1 - top source bound
     * @param x2 - right source bound
     * @param y2 - bottom source bound
     * @throws BoardNotBuildException - Thrown when the board has not been initialized (no tiles exist)
     * @throws InvalidTileException - Thrown when there is at least one tile that is not within the board.
     * @throws ItemAlreadyOnTileException - Thrown when the item is already present on the given tile.
     */
    void addWall(int x1, int y1, int x2, int y2) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException;

    /**
     * Adds a wall to a given tile
     * @param x - x coordinate
     * @param y - y coordinate
     * @throws BoardNotBuildException - Thrown when the board has not been initialized (no tiles exist)
     * @throws InvalidTileException - Thrown when there is at least one tile that is not within the board.
     * @throws ItemAlreadyOnTileException - Thrown when the item is already present on the given tile.
     */
    void addWall(int x, int y) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException;

    /**
     * Assign an area as guard spawn area
     * @param x1 - left bound
     * @param y1 - top bound
     * @param x2 - right bound
     * @param y2 - lower bound
     * @throws BoardNotBuildException - Thrown when the board has not been initialized (no tiles exist)
     * @throws InvalidTileException - Thrown when there is at least one tile that is not within the board.
     */
    void addGuardSpawnArea(int x1, int y1, int x2, int y2) throws BoardNotBuildException, InvalidTileException;
    void addGuardSpawnArea(int x1, int y1) throws BoardNotBuildException, InvalidTileException;
    /**
     * Assign an area as intruder spawn area
     * @param x1 - left bound
     * @param y1 - top bound
     * @param x2 - right bound
     * @param y2 - lower bound
     * @throws BoardNotBuildException - Thrown when the board has not been initialized (no tiles exist)
     * @throws InvalidTileException - Thrown when there is at least one tile that is not within the board.
     */
    void addIntruderSpawnArea(int x1, int y1, int x2, int y2) throws BoardNotBuildException, InvalidTileException;
    void addIntruderSpawnArea(int x1, int y1) throws BoardNotBuildException, InvalidTileException;
    Tile getTargetCenter();

    void addMarker(Marker.MarkerType type, int x1, int y1, Player player) throws BoardNotBuildException, InvalidTileException, ItemAlreadyOnTileException;

    Tile[] calculateNeigboringTiles(Marker marker) throws InvalidTileException, BoardNotBuildException;

    void removeMarker(MarkerSmell markersmell) throws BoardNotBuildException, InvalidTileException, ItemNotOnTileException;

    void checkMarkers() throws BoardNotBuildException, InvalidTileException, ItemNotOnTileException;

    ArrayList<MarkerSmell> getListOfPlacedMarkers();

    TileArea getBoard();

    void setBoard(TileArea board);

    void setBoard(HashMap<Integer, HashMap<Integer, Tile>> board);

    TileArea getTargetArea();

    void setTargetArea(TileArea targetArea);

    TileArea getGuardSpawnArea();

    void setGuardSpawnArea(TileArea guardSpawnArea);

    TileArea getIntruderSpawnArea();

    void setIntruderSpawnArea(TileArea intruderSpawnArea);

    IGameRepository getGameRepository();

    void setGameRepository(IGameRepository gameRepository);

    IPlayerRepository getPlayerRepository();

    void setPlayerRepository(IPlayerRepository playerRepository);
}
