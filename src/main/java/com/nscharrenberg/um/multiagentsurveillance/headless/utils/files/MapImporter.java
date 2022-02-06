package com.nscharrenberg.um.multiagentsurveillance.headless.utils.files;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MapImporter {
    public static void load(String path) throws IOException {
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

    private static void parseLine(String currentLine) {
        // Delimit line by "="
        String[] split = currentLine.split("=");

        // Retrieve ID and Value and remove excess spaces
        String id = split[0].trim();
        String value = split[1].trim();

        if (isConfiguration(id)) {
            addToConfig(id, value);
            return;
        }

        if (isMap(id)) {
            // TODO: Map Logic
            try {
                addToMap(id, value);
            } catch (InvalidTileException | BoardNotBuildException e) {
                e.printStackTrace();
            }
            return;
        }

        // Invalid Item - For now skip the item
    }

    private static void addToConfig(String id, String value) {
        if (id.equals(FileItems.NAME.getKey())) {
            Factory.getGameRepository().setName(value);
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
        }
    }

    private static void addToMap(String id, String value) throws InvalidTileException, BoardNotBuildException {
        for (int i = 0; i < Factory.getGameRepository().getHeight(); i++) {
            for (int j = 0; j < Factory.getGameRepository().getWidth(); i++) {
                Factory.getMapRepository().getBoard().add(new Tile(i, j));
            }
        }

//        // Delimit by " " when multiple parameters are passed in one-line
//        String[] items = value.split(" ");
//
//        if (id.equals(FileItems.TARGET_AREA.getKey())) {
//            int x1 = Integer.parseInt(items[0]);
//            int y1 = Integer.parseInt(items[1]);
//            int x2 = Integer.parseInt(items[2]);
//            int y2 = Integer.parseInt(items[3]);
//
//            Factory.getMapRepository().addTargetArea(x1, y1, x2, y2);
//        } else if (id.equals(FileItems.SPAWN_AREA_INTRUDERS.getKey())) {
//            int x1 = Integer.parseInt(items[0]);
//            int y1 = Integer.parseInt(items[1]);
//            int x2 = Integer.parseInt(items[2]);
//            int y2 = Integer.parseInt(items[3]);
//
//            Factory.getMapRepository().addIntruderSpawnArea(x1, y1, x2, y2);
//        } else if (id.equals(FileItems.SPAWN_AREA_GUARDS.getKey())) {
//            int x1 = Integer.parseInt(items[0]);
//            int y1 = Integer.parseInt(items[1]);
//            int x2 = Integer.parseInt(items[2]);
//            int y2 = Integer.parseInt(items[3]);
//
//            Factory.getMapRepository().addGuardSpawnArea(x1, y1, x2, y2);
//        }
    }

    /**
     * Check if the ID is part of the game configuration
     * @param id - file item ID
     * @return - Whether the given id is a configuration
     */
    private static boolean isConfiguration(String id) {
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
    private static boolean isMap(String id) {
        return id.equals(FileItems.WALL.getKey())
                        || id.equals(FileItems.TELEPORT.getKey())
                        || id.equals(FileItems.SHADED.getKey())
                        || id.equals(FileItems.TEXTURE.getKey());
    }

    private MapImporter() {}
}
