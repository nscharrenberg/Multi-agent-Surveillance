package com.nscharrenberg.um.multiagentsurveillance.headless.utils.files;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;

import java.io.IOException;

public abstract class Importer {
    protected IGameRepository gameRepository;
    protected IMapRepository mapRepository;
    protected IPlayerRepository playerRepository;

    public Importer(IGameRepository gameRepository, IMapRepository mapRepository, IPlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.mapRepository = mapRepository;
        this.playerRepository = playerRepository;
    }

    public Importer() {
        gameRepository = Factory.getGameRepository();
        playerRepository = Factory.getPlayerRepository();
        mapRepository = Factory.getMapRepository();
    }

    public abstract void load(String path) throws IOException;

    public boolean isConfiguration(String id) {
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
                || id.equals(FileItems.SPAWN_AREA_INTRUDERS.getKey())
                || id.equals(FileItems.DISTANCE_SOUND_ROTATING.getKey())
                || id.equals(FileItems.DISTANCE_SOUND_SPRINTING.getKey())
                || id.equals(FileItems.DISTANCE_SOUND_WAITING.getKey())
                || id.equals(FileItems.DISTANCE_SOUND_WALKING.getKey())
                || id.equals(FileItems.DISTANCE_SOUND_YELLING.getKey())
                || id.equals(FileItems.DISTANCE_VIEWING.getKey())
                || id.equals(FileItems.MARKERS_ENABLED.getKey())
                || id.equals(FileItems.HEAR_THROUGH_WALLS_ENABLED.getKey());
    }

    /**
     * Checks if the ID is part of the map generation
     * @param id - file item ID
     * @return - Whether the given id is a map generation
     */
    public boolean isMap(String id) {
        return id.equals(FileItems.WALL.getKey())
                || id.equals(FileItems.TELEPORT.getKey())
                || id.equals(FileItems.SHADED.getKey())
                || id.equals(FileItems.TEXTURE.getKey())
                || id.equals(FileItems.DEADEND_MARKER.getKey())
                || id.equals(FileItems.WALL_TEXTURE.getKey())
                || id.equals(FileItems.FLOOR_TEXTURE.getKey())
                || id.equals(FileItems.GUARD_SPAWN_TEXTURE.getKey())
                || id.equals(FileItems.INTRUDER_SPAWN_TEXTURE.getKey())
                || id.equals(FileItems.TELEPORT_SOURCE_TEXTURE.getKey())
                || id.equals(FileItems.TELEPORT_DESTINATION_TEXTURE.getKey())
                || id.equals(FileItems.TARGET_AREA_TEXTURE.getKey())
                || id.equals(FileItems.SHADED_TEXTURE.getKey())
                || id.equals(FileItems.MAP_DATA.getKey());
    }
}
