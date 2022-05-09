package com.nscharrenberg.um.multiagentsurveillance.headless.exceptions;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Area;

public class InvalidTileException extends Exception {

    /**
     * Construct a new exception with "The bounaaries of the area (...) could not be found" as its detail message.
     * The cause of this exception would be that the area is empty, although this should not happen.
     * @param area - the area of which the bounds could not be found.
     */
    public InvalidTileException(Area<?> area) {
        super("The boundaries of the area (" + area + ") could not be found");
    }

    /**
     * Construct a new exception with "Tile on (x, y) could not be found" as its detail message.
     * The cause of this exception would be that the tile is not within the scope of the board.
     *
     * @param x - the row the tile should be on
     * @param y - the column the tile should be on
     */
    public InvalidTileException(int x, int y) {
        super("Tile on (" + x + ", " + y + ") could not be found");
    }

    /**
     * Construct a new exception with "Area between (x1, y1) and (x2, y2) could not be found" as its detail message.
     * The cause of this exception would be that at least one of the tiles between (x1, y1) and (x2, y2) is not within the scope of the board.
     *
     * @param x1 - The left bound row the tile should be on
     * @param y1 - The top bound column the tile should be on
     * @param x2 - The right bound row the tile should be on
     * @param y2 - the bottom bound column the tile should be on
     */
    public InvalidTileException(int x1, int y1, int x2, int y2) {
        super("Area between (" + x1 + ", " + y1 + ") and (" + x2 + ", " + y2 + ") could not be found");
    }

    /**
     * Construct a new exception with "Area between (x1, y1) and (x2, y2) could not be found" as its detail message.
     * The cause of this exception would be that at least one of the tiles between (x1, y1) and (x2, y2) is not within the scope of the area.
     *
     * @param x1 - The left bound row the tile should be on
     * @param y1 - The top bound column the tile should be on
     * @param x2 - The right bound row the tile should be on
     * @param y2 - the bottom bound column the tile should be on
     * @param area - the area that could the coordinates could not be found in
     */
    public InvalidTileException(int x1, int y1, int x2, int y2, Area<?> area) {
        super("A subset between (" + x1 + ", " + y1 + ") and (" + x2 + ", " + y2 + ") could not be found within the given area (" + area.getBounds().toString() + ")");
    }
}
