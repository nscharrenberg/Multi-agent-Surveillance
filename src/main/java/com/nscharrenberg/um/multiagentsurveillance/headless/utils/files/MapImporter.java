package com.nscharrenberg.um.multiagentsurveillance.headless.utils.files;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameMode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AngleConverter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MapImporter {
    private boolean tilesInitialized = false;

    /**
     * Load a file into the game
     * @param path - the file path
     * @throws IOException - Thrown when the file could not be found or is unable to read the file
     */
    public void load(String path) throws IOException {
        // Reset the game state
        Factory.reset();

        try (FileInputStream fileInputStream = new FileInputStream(path); Scanner scanner = new Scanner(fileInputStream, StandardCharsets.UTF_8)) {
            while (scanner.hasNextLine()) {
                parseLine(scanner.nextLine());
            }

            if (scanner.ioException() != null) {
                throw scanner.ioException();
            }
        }
    }

    /**
     * Parses a line from the imported file
     * @param currentLine - the line to be parsed
     */
    private void parseLine(String currentLine) {
        // Delimit line by "="
        String[] split = currentLine.split("=");

        // Retrieve ID and Value and remove excess spaces
        String id = split[0].trim();
        String value = split[1].trim();
        value = value.split("//")[0].trim();

        if (isConfiguration(id)) {
            try {
                addToConfig(id, value);
            } catch (InvalidTileException | BoardNotBuildException e) {
                e.printStackTrace();
            }
        } else if (isMap(id)) {
            try {
                addToMap(id, value);
            } catch (BoardNotBuildException | ItemAlreadyOnTileException | InvalidTileException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Add Configuration items to the repository
     * @param id - the item type to be added
     * @param value - the value of the item to be added
     * @throws BoardNotBuildException - Thrown when the board has not been build (no tiles exist)
     * @throws InvalidTileException - Thrown when the tile is outside the board.
     */
    private void addToConfig(String id, String value) throws InvalidTileException, BoardNotBuildException {
        if (id.equals(FileItems.NAME.getKey())) {
            Factory.getGameRepository().setName(value);
        } else if (id.equals(FileItems.GAME_MODE.getKey())) {
            Factory.getGameRepository().setGameMode(GameMode.getById(Integer.parseInt(value)));
        } else if (id.equals(FileItems.HEIGHT.getKey())) {
            Factory.getGameRepository().setHeight(Integer.parseInt(value));
        } else if (id.equals(FileItems.WIDTH.getKey())) {
            Factory.getGameRepository().setWidth(Integer.parseInt(value));
        } else if (id.equals(FileItems.SCALING.getKey())) {
            Factory.getGameRepository().setScaling(Double.parseDouble(value));
        } else if (id.equals(FileItems.NUM_GUARDS.getKey())) {
            Factory.getGameRepository().setGuardCount(Integer.parseInt(value));
        } else if (id.equals(FileItems.NUM_INTRUDERS.getKey())) {
            Factory.getGameRepository().setIntruderCount(Integer.parseInt(value));
        } else if (id.equals(FileItems.BASE_SPEED_GUARD.getKey())) {
            Factory.getGameRepository().setBaseSpeedGuards(Double.parseDouble(value));
        } else if (id.equals(FileItems.BASE_SPEED_INTRUDER.getKey())) {
            Factory.getGameRepository().setBaseSpeedIntruders(Double.parseDouble(value));
        } else if (id.equals(FileItems.SPRINT_SPEED_INTRUDER.getKey())) {
            Factory.getGameRepository().setSpringSpeedIntruders(Double.parseDouble(value));
        } else if (id.equals(FileItems.TIME_STEP.getKey())) {
            Factory.getGameRepository().setTimeStep(Double.parseDouble(value));
        } else {
            if (!this.tilesInitialized && Factory.getGameRepository().getHeight() > 0 && Factory.getGameRepository().getWidth() > 0) {
                initTiles();

                this.tilesInitialized = true;
            }

            // Delimit by " " when multiple parameters are passed in one-line
            String[] items = value.split(" ");

            if (id.equals(FileItems.TARGET_AREA.getKey())) {
                int x1 = Integer.parseInt(items[0]);
                int y1 = Integer.parseInt(items[1]);
                int x2 = Integer.parseInt(items[2]);
                int y2 = Integer.parseInt(items[3]);

                Factory.getMapRepository().addTargetArea(x1, y1, x2, y2);
            } else if (id.equals(FileItems.SPAWN_AREA_INTRUDERS.getKey())) {
                int x1 = Integer.parseInt(items[0]);
                int y1 = Integer.parseInt(items[1]);
                int x2 = Integer.parseInt(items[2]);
                int y2 = Integer.parseInt(items[3]);

                Factory.getMapRepository().addIntruderSpawnArea(x1, y1, x2, y2);
            } else if (id.equals(FileItems.SPAWN_AREA_GUARDS.getKey())) {
                int x1 = Integer.parseInt(items[0]);
                int y1 = Integer.parseInt(items[1]);
                int x2 = Integer.parseInt(items[2]);
                int y2 = Integer.parseInt(items[3]);

                Factory.getMapRepository().addGuardSpawnArea(x1, y1, x2, y2);
            }
        }
    }

    /**
     * Initialize the tiles based on the width and height
     */
    private void initTiles() {
        Factory.getMapRepository().setBoard(new TileArea());
        Factory.getMapRepository().buildEmptyBoard();
    }

    /**
     * Adds map item to the repository
     * @param id - the item type to be added
     * @param value - the value of the item to be added
     * @throws BoardNotBuildException - Thrown when the board has not been build (no tiles exist)
     * @throws ItemAlreadyOnTileException - Thrown when the item is already present on the given tile.
     * @throws InvalidTileException - Thrown when the tile is outside the board.
     */
    private void addToMap(String id, String value) throws BoardNotBuildException, ItemAlreadyOnTileException, InvalidTileException {
        if (Factory.getMapRepository().getBoard().isEmpty()) {
            throw new BoardNotBuildException();
        }

        // Delimit by " " when multiple parameters are passed in one-line
        String[] items = value.split(" ");

        if (id.equals(FileItems.WALL.getKey())) {
            int x1 = Integer.parseInt(items[0]);
            int y1 = Integer.parseInt(items[1]);
            int x2 = Integer.parseInt(items[2]);
            int y2 = Integer.parseInt(items[3]);

            Factory.getMapRepository().addWall(x1, y1, x2, y2);
        } else if (id.equals(FileItems.TELEPORT.getKey())) {
            int x1 = Integer.parseInt(items[0]);
            int y1 = Integer.parseInt(items[1]);
            int x2 = Integer.parseInt(items[2]);
            int y2 = Integer.parseInt(items[3]);
            int destX = Integer.parseInt(items[4]);
            int destY = Integer.parseInt(items[5]);
            int angle = Integer.parseInt(items[0]);
            Angle roundedAngle = AngleConverter.convert(angle);

            Factory.getMapRepository().addTeleporter(x1, y1, x2, y2, destX, destY, roundedAngle);
        } else if (id.equals(FileItems.SHADED.getKey())) {
            int x1 = Integer.parseInt(items[0]);
            int y1 = Integer.parseInt(items[1]);
            int x2 = Integer.parseInt(items[2]);
            int y2 = Integer.parseInt(items[3]);

            Factory.getMapRepository().addShaded(x1, y1, x2, y2);
        } else if (id.equals(FileItems.TEXTURE.getKey())) {
            // TODO: Implement once we know what it is
        }
    }

    /**
     * Check if the ID is part of the game configuration
     * @param id - file item ID
     * @return - Whether the given id is a configuration
     */
    private boolean isConfiguration(String id) {
        return id.equals(FileItems.NAME.getKey())
                        || id.equals(FileItems.GAME_MODE.getKey())
                        || id.equals(FileItems.HEIGHT.getKey())
                        || id.equals(FileItems.WIDTH.getKey())
                        || id.equals(FileItems.SCALING.getKey())
                        || id.equals(FileItems.NUM_GUARDS.getKey())
                        || id.equals(FileItems.NUM_INTRUDERS.getKey())
                        || id.equals(FileItems.BASE_SPEED_GUARD.getKey())
                        || id.equals(FileItems.BASE_SPEED_INTRUDER.getKey())
                        || id.equals(FileItems.SPRINT_SPEED_INTRUDER.getKey())
                        || id.equals(FileItems.TIME_STEP.getKey())
                        || id.equals(FileItems.TARGET_AREA.getKey())
                        || id.equals(FileItems.SPAWN_AREA_GUARDS.getKey())
                        || id.equals(FileItems.SPAWN_AREA_INTRUDERS.getKey());
    }

    /**
     * Checks if the ID is part of the map generation
     * @param id - file item ID
     * @return - Whether the given id is a map generation
     */
    private boolean isMap(String id) {
        return id.equals(FileItems.WALL.getKey())
                        || id.equals(FileItems.TELEPORT.getKey())
                        || id.equals(FileItems.SHADED.getKey())
                        || id.equals(FileItems.TEXTURE.getKey());
    }
}
