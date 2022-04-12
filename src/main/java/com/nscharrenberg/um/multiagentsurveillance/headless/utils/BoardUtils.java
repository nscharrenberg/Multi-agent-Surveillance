package com.nscharrenberg.um.multiagentsurveillance.headless.utils;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.AdvancedAngle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Area;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

import java.util.HashMap;
import java.util.Optional;

public class BoardUtils {

    /**
     * Evaluate a tile next to your current tile.
     * @param board - the board with all the tiles
     * @param tile - the current tile to be evaluated
     * @param direction - the direction (the tile to your left, right, up or down)
     * @return Nothing or the next position
     */
    public static Optional<Tile> nextPosition(Area<Tile> board, Tile tile, Angle direction) {
        int nextX = tile.getX() + direction.getxIncrement();
        int nextY = tile.getY() + direction.getyIncrement();

        Optional<Tile> currentTileOpt = board.getByCoordinates(tile.getX(), tile.getY());

        // Current tile doesn't exist in knowledge --> Shouldn't happen
        if (currentTileOpt.isEmpty()) {
            return Optional.empty();
        }

        Optional<Tile> nextTileOpt = board.getByCoordinates(nextX, nextY);

        if (nextTileOpt.isEmpty()) {
            return Optional.empty();
        }

        return nextTileOpt;
    }

    /**
     * Evaluate a tile next to your current tile.
     * @param board - the board with all the tiles
     * @param tile - the current tile to be evaluated
     * @param direction - the direction (the tile to your left, right, up or down)
     * @return Nothing or the next position
     */
    public static Optional<Tile> nextPosition(Area<Tile> board, Tile tile, AdvancedAngle direction) {
        int nextX = tile.getX() + direction.getxIncrement();
        int nextY = tile.getY() + direction.getyIncrement();

        Optional<Tile> currentTileOpt = board.getByCoordinates(tile.getX(), tile.getY());

        // Current tile doesn't exist in knowledge --> Shouldn't happen
        if (currentTileOpt.isEmpty()) {
            return Optional.empty();
        }

        Optional<Tile> nextTileOpt = board.getByCoordinates(nextX, nextY);

        if (nextTileOpt.isEmpty()) {
            return Optional.empty();
        }

        return nextTileOpt;
    }

    public static HashMap<AdvancedAngle, Tile> getNeighbours(Area<Tile> board, Tile tile) {
        HashMap<AdvancedAngle, Tile> neighbours = new HashMap<>();

        for (AdvancedAngle angle : AdvancedAngle.values()) {
            Tile neighbourTile = null;

            Optional<Tile> neighbourTileOpt = BoardUtils.nextPosition(board, tile, angle);

            if (neighbourTileOpt.isPresent()) {
                neighbourTile = neighbourTileOpt.get();
            }

            neighbours.put(angle, neighbourTile);
        }

        return neighbours;
    }

    public static boolean isSurrounded(Area<Tile> board, Tile tile) {

        for (AdvancedAngle angle : AdvancedAngle.values()) {
            Tile neighbourTile;

            Optional<Tile> neighbourTileOpt = BoardUtils.nextPosition(board, tile, angle);

            if (neighbourTileOpt.isPresent()) {
                neighbourTile = neighbourTileOpt.get();
                if (!neighbourTile.isCollision() && !neighbourTile.isTeleport()) {
                    return false;
                }
            }
        }

        return true;
    }
}
