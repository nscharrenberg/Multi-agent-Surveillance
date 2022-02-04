package com.nscharrenberg.um.multiagentsurveillance.headless.utils.files;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;

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
            addToMap(id, value);
            return;
        }

        // Invalid Item - For now skip the item
    }

    private static void addToConfig(String id, String value) {
        if (id.equals(FileItems.NAME.getKey())) {
            Factory.getGameRepository().setName(value);
            return;
        }

        if (id.equals(FileItems.HEIGHT.getKey())) {
            Factory.getGameRepository().setHeight(Integer.parseInt(value));
            return;
        }

        if (id.equals(FileItems.WIDTH.getKey())) {
            Factory.getGameRepository().setWidth(Integer.parseInt(value));
        }



        // Delimit by " " when multiple parameters are passed in one-line
        String[] items = value.split(" ");

        // TODO: Items with multiple parameters
    }

    private static void addToMap(String id, String value) {
        // TODO: add logic
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
