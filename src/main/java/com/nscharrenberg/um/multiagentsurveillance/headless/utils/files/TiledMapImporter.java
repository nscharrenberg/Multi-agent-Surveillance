package com.nscharrenberg.um.multiagentsurveillance.headless.utils.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameMode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AngleConverter;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class TiledMapImporter extends Importer{
    private boolean tilesInitialized = false;
    private Gson gson;

    private ArrayList<Double> wallTextures = new ArrayList<>();
    private ArrayList<Double> guardSpawnTextures = new ArrayList<>();
    private ArrayList<Double> intruderSpawnTextures = new ArrayList<>();
    private ArrayList<Double> teleportDestinationTextures = new ArrayList<>();
    private ArrayList<Double> teleportSourceTextures = new ArrayList<>();
    private ArrayList<Double> targetAreaTextures = new ArrayList<>();
    private ArrayList<Double> shadedTextures = new ArrayList<>();

    public TiledMapImporter(IGameRepository gameRepository, IMapRepository mapRepository, IPlayerRepository playerRepository) {
        super(gameRepository, mapRepository, playerRepository);
        gson = buildGson();
    }

    public TiledMapImporter() {
        super();

        gson = buildGson();
    }

    @Override
    public void load(String path) throws IOException {
        FileReader fr = new FileReader(path);

        Map<String, Object> data = gson.fromJson(fr, Map.class);

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();

            if (isConfiguration(key)) {
                try {
                    addToConfig(key, entry.getValue());
                } catch (InvalidTileException | BoardNotBuildException | ItemAlreadyOnTileException e) {
                    throw new RuntimeException(e);
                }
            }

            if (isMap(key)) {
                try {
                    addToMap(key, entry.getValue());
                } catch (BoardNotBuildException | ItemAlreadyOnTileException | InvalidTileException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void addToConfig(String id, Object value) throws InvalidTileException, BoardNotBuildException, ItemAlreadyOnTileException {
        if (id.equals(FileItems.NAME.getKey())) {
            gameRepository.setName((String) value);
        } else if (id.equals(FileItems.GAME_MODE.getKey())) {
            gameRepository.setGameMode(GameMode.getById(((Double) value).intValue()));
        } else if (id.equals(FileItems.HEIGHT.getKey())) {
            gameRepository.setHeight(((Double) value).intValue());
        } else if (id.equals(FileItems.WIDTH.getKey())) {
            gameRepository.setWidth(((Double) value).intValue());
        } else if (id.equals(FileItems.SCALING.getKey())) {
            gameRepository.setScaling((Double) value);
        } else if (id.equals(FileItems.NUM_GUARDS.getKey())) {
            gameRepository.setGuardCount(((Double) value).intValue());
        } else if (id.equals(FileItems.NUM_INTRUDERS.getKey())) {
            gameRepository.setIntruderCount(((Double) value).intValue());
        } else if (id.equals(FileItems.BASE_SPEED_GUARD.getKey())) {
            gameRepository.setBaseSpeedGuards((Double) value);
        } else if (id.equals(FileItems.BASE_SPEED_INTRUDER.getKey())) {
            gameRepository.setBaseSpeedIntruders((Double) value);
        } else if (id.equals(FileItems.SPRINT_SPEED_INTRUDER.getKey())) {
            gameRepository.setSpringSpeedIntruders((Double) value);
        } else if (id.equals(FileItems.TIME_STEP.getKey())) {
            gameRepository.setTimeStep((Double) value);
        } else if (id.equals(FileItems.DISTANCE_SOUND_YELLING.getKey())) {
            gameRepository.setDistanceSoundYelling((Double) value);
        } else if (id.equals(FileItems.DISTANCE_SOUND_WALKING.getKey())) {
            gameRepository.setDistanceSoundWalking((Double) value);
        } else if (id.equals(FileItems.DISTANCE_SOUND_WAITING.getKey())) {
            gameRepository.setDistanceSoundWaiting((Double) value);
        } else if (id.equals(FileItems.DISTANCE_SOUND_SPRINTING.getKey())) {
            gameRepository.setDistanceSoundSprinting((Double) value);
        } else if (id.equals(FileItems.DISTANCE_SOUND_ROTATING.getKey())) {
            gameRepository.setDistanceSoundRotating((Double) value);
        } else if (id.equals(FileItems.DISTANCE_VIEWING.getKey())) {
            gameRepository.setDistanceViewing((Double) value);
        } else if (id.equals(FileItems.MARKERS_ENABLED.getKey())) {
            gameRepository.setCanPlaceMarkers((Boolean) value);
        } else if (id.equals(FileItems.HEAR_THROUGH_WALLS_ENABLED.getKey())) {
            gameRepository.setCanHearThroughWalls((Boolean) value);
        } else {
            initCheck();
        }
    }

    private boolean initCheck() {
        if (!this.tilesInitialized && gameRepository.getHeight() > 0 && gameRepository.getWidth() > 0) {
            initTiles();

            this.tilesInitialized = true;

            return true;
        }

        return false;
    }

    private void addToMap(String id, Object value) throws BoardNotBuildException, ItemAlreadyOnTileException, InvalidTileException {
        if (mapRepository.getBoard().isEmpty() && !initCheck()) {
            throw new BoardNotBuildException();
        }

        // Read Textures
        if (id.equals(FileItems.WALL_TEXTURE.getKey())) {
            wallTextures = (ArrayList<Double>) value;
        } else if (id.equals(FileItems.GUARD_SPAWN_TEXTURE.getKey())) {
            guardSpawnTextures = (ArrayList<Double>) value;
        } else if (id.equals(FileItems.INTRUDER_SPAWN_TEXTURE.getKey())) {
            intruderSpawnTextures = (ArrayList<Double>) value;
        } else if (id.equals(FileItems.TELEPORT_DESTINATION_TEXTURE.getKey())) {
            teleportDestinationTextures = (ArrayList<Double>) value;
        } else if (id.equals(FileItems.TELEPORT_SOURCE_TEXTURE.getKey())) {
            teleportSourceTextures = (ArrayList<Double>) value;
        } else if (id.equals(FileItems.TARGET_AREA_TEXTURE.getKey())) {
            targetAreaTextures = (ArrayList<Double>) value;
        } else if (id.equals(FileItems.SHADED_TEXTURE.getKey())) {
            shadedTextures = (ArrayList<Double>) value;
        } else if (id.equals(FileItems.TELEPORT.getKey())) {
            ArrayList<ArrayList<Double>> teleporters = (ArrayList<ArrayList<Double>>) value;

            for (ArrayList<Double> teleporter : teleporters) {
                Double sourceX = teleporter.get(0);
                Double sourceY = teleporter.get(1);
                Double destX = teleporter.get(2);
                Double destY = teleporter.get(3);
                Double directionAngle = teleporter.get(4);
                Action roundedAction = AngleConverter.convert(directionAngle);

                mapRepository.addTeleporter(sourceX.intValue(), sourceY.intValue(), destX.intValue(), destY.intValue(), roundedAction);            }
        } else if (id.equals(FileItems.MAP_DATA.getKey())) {
            // Read Map Data
            ArrayList<Double> items = (ArrayList<Double>) value;

            int col = 0;
            int row = 0;

            for (Double item : items) {
                int x1 = col;
                int y1 = row;
                int x2 = col+1;
                int y2 = row+1;

                if (wallTextures.contains(item)) {
                    mapRepository.addWall(x1, y1);
                } else if (guardSpawnTextures.contains(item)) {
                    mapRepository.addGuardSpawnArea(x1, y1);
                } else if (intruderSpawnTextures.contains(item)) {
                    mapRepository.addIntruderSpawnArea(x1, y1);
                } else if (targetAreaTextures.contains(item)) {
                    mapRepository.addTargetArea(x1, y1);
                } else if (shadedTextures.contains(item)) {
                    mapRepository.addShaded(x1, y1);
                }

                if (col >= gameRepository.getWidth()-1) {
                    col = 0;
                    row++;
                } else {
                    col++;
                }
            }
        }
    }

    private void initTiles() {
        mapRepository.setBoard(new TileArea());
        mapRepository.buildEmptyBoard();
    }

    private Gson buildGson() {
        GsonBuilder builder = new GsonBuilder();

        return builder.create();
    }


}
