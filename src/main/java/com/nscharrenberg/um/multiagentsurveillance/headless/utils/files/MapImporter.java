package com.nscharrenberg.um.multiagentsurveillance.headless.utils.files;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameMode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Marker;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AngleConverter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MapImporter extends Importer {
    private boolean tilesInitialized = false;

    public MapImporter(IGameRepository gameRepository, IMapRepository mapRepository, IPlayerRepository playerRepository) {
        super(gameRepository, mapRepository,playerRepository);
    }

    public MapImporter() {
        super();
    }

    /**
     * Load a file into the game
     * @param path - the file path
     * @throws IOException - Thrown when the file could not be found or is unable to read the file
     */
    @Override
    public void load(String path) throws IOException {
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
    public void parseLine(String currentLine) {
        // Delimit line by "="
        String[] split = currentLine.split("=");

        // Retrieve ID and Value and remove excess spaces
        String id = split[0].trim();
        String value = split[1].trim();
        value = value.split("//")[0].trim();

        if (isConfiguration(id)) {
            try {
                addToConfig(id, value);
            } catch (InvalidTileException | BoardNotBuildException | ItemAlreadyOnTileException e) {
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
    private void addToConfig(String id, String value) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException {
        if (id.equals(FileItems.NAME.getKey())) {
            gameRepository.setName(value);
        } else if (id.equals(FileItems.GAME_MODE.getKey())) {
            gameRepository.setGameMode(GameMode.getById(Integer.parseInt(value)));
        } else if (id.equals(FileItems.HEIGHT.getKey())) {
            gameRepository.setHeight(Integer.parseInt(value));
        } else if (id.equals(FileItems.WIDTH.getKey())) {
            gameRepository.setWidth(Integer.parseInt(value));
        } else if (id.equals(FileItems.SCALING.getKey())) {
            gameRepository.setScaling(Double.parseDouble(value));
        } else if (id.equals(FileItems.NUM_GUARDS.getKey())) {
            gameRepository.setGuardCount(Integer.parseInt(value));
        } else if (id.equals(FileItems.NUM_INTRUDERS.getKey())) {
            gameRepository.setIntruderCount(Integer.parseInt(value));
        } else if (id.equals(FileItems.BASE_SPEED_GUARD.getKey())) {
            gameRepository.setBaseSpeedGuards(Double.parseDouble(value));
        } else if (id.equals(FileItems.BASE_SPEED_INTRUDER.getKey())) {
            gameRepository.setBaseSpeedIntruders(Double.parseDouble(value));
        } else if (id.equals(FileItems.SPRINT_SPEED_INTRUDER.getKey())) {
            gameRepository.setSpringSpeedIntruders(Double.parseDouble(value));
        } else if (id.equals(FileItems.TIME_STEP.getKey())) {
            gameRepository.setTimeStep(Double.parseDouble(value));
        } else {
            if (!this.tilesInitialized && gameRepository.getHeight() > 0 && gameRepository.getWidth() > 0) {
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

                mapRepository.addTargetArea(x1, y1, x2, y2);
            } else if (id.equals(FileItems.SPAWN_AREA_INTRUDERS.getKey())) {
                int x1 = Integer.parseInt(items[0]);
                int y1 = Integer.parseInt(items[1]);
                int x2 = Integer.parseInt(items[2]);
                int y2 = Integer.parseInt(items[3]);

                mapRepository.addIntruderSpawnArea(x1, y1, x2, y2);
            } else if (id.equals(FileItems.SPAWN_AREA_GUARDS.getKey())) {
                int x1 = Integer.parseInt(items[0]);
                int y1 = Integer.parseInt(items[1]);
                int x2 = Integer.parseInt(items[2]);
                int y2 = Integer.parseInt(items[3]);

                mapRepository.addGuardSpawnArea(x1, y1, x2, y2);
            }
        }
    }

    /**
     * Initialize the tiles based on the width and height
     */
    private void initTiles() {
        mapRepository.setBoard(new TileArea());
        mapRepository.buildEmptyBoard();
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
        if (mapRepository.getBoard().isEmpty()) {
            throw new BoardNotBuildException();
        }

        // Delimit by " " when multiple parameters are passed in one-line
        String[] items = value.split(" ");

        if (id.equals(FileItems.WALL.getKey())) {
            int x1 = Integer.parseInt(items[0]);
            int y1 = Integer.parseInt(items[1]);
            int x2 = Integer.parseInt(items[2]);
            int y2 = Integer.parseInt(items[3]);

            mapRepository.addWall(x1, y1, x2, y2);
        } else if (id.equals(FileItems.TELEPORT.getKey())) {
            int x1 = Integer.parseInt(items[0]);
            int y1 = Integer.parseInt(items[1]);
            int x2 = Integer.parseInt(items[2]);
            int y2 = Integer.parseInt(items[3]);
            int destX = Integer.parseInt(items[4]);
            int destY = Integer.parseInt(items[5]);
            int angle = Integer.parseInt(items[0]);
            Action roundedAction = AngleConverter.convert(angle);

            mapRepository.addTeleporter(x1, y1, x2, y2, destX, destY, roundedAction);
        } else if (id.equals(FileItems.SHADED.getKey())) {
            int x1 = Integer.parseInt(items[0]);
            int y1 = Integer.parseInt(items[1]);
            int x2 = Integer.parseInt(items[2]);
            int y2 = Integer.parseInt(items[3]);

            mapRepository.addShaded(x1, y1, x2, y2);
        } else if (id.equals(FileItems.DEADEND_MARKER.getKey())) {
            int x1 = Integer.parseInt(items[0]);
            int y1 = Integer.parseInt(items[1]);

            mapRepository.addMarker(Marker.MarkerType.DEAD_END, x1, y1, null);
        } else if (id.equals(FileItems.TEXTURE.getKey())) {
            // TODO: Implement once we know what it is
        }
    }

    /**
     * Check if the ID is part of the game configuration
     * @param id - file item ID
     * @return - Whether the given id is a configuration
     */

}
