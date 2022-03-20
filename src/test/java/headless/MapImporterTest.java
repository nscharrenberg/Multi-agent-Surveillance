package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.CollisionException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import com.rits.cloning.Cloner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

public class MapImporterTest {
    @DisplayName("Decoupled Repository Import Successful")
    @Test
    void testDecoupledRepositoriesSuccessful() {
        File file = new File("src/test/resources/maps/testmap.txt");

        if (!file.exists()) {
            Assertions.fail("Resource not found");
        }

        String path = file.getAbsolutePath();

        MapImporter importer = new MapImporter();

        try {
            importer.load(path);

            Factory.getPlayerRepository().spawn(Guard.class);

            Cloner cloner = new Cloner();
            cloner.dontCloneInstanceOf(SecureRandom.class);
            cloner.dontCloneInstanceOf(PriorityQueue.class);
            IMapRepository mapRepository = cloner.deepClone(Factory.getMapRepository());
            IGameRepository gameRepository = cloner.deepClone(Factory.getGameRepository());
            IPlayerRepository playerRepository = cloner.deepClone(Factory.getPlayerRepository());

            mapRepository.setGameRepository(gameRepository);
            mapRepository.setPlayerRepository(playerRepository);
            playerRepository.setGameRepository(gameRepository);
            playerRepository.setMapRepository(mapRepository);



            Assertions.assertEquals(Factory.getPlayerRepository().getGuards().get(0), playerRepository.getGuards().get(0));

            playerRepository.move(playerRepository.getGuards().get(0), Angle.DOWN);
            playerRepository.move(playerRepository.getGuards().get(0), Angle.DOWN);

            Assertions.assertNotEquals(Factory.getPlayerRepository().getGuards().get(0), playerRepository.getGuards().get(0));
        } catch (IOException | CollisionException | ItemAlreadyOnTileException | InvalidTileException | ItemNotOnTileException e) {
            Assertions.fail();
        }
    }

    @DisplayName("Map Import Successful")
    @Test
    void testMapImportSuccessful() {
        File file = new File("src/test/resources/maps/testmap.txt");

        if (!file.exists()) {
            Assertions.fail("Resource not found");
        }

        String path = file.getAbsolutePath();

        MapImporter importer = new MapImporter();

        try {
            importer.load(path);

            Assertions.assertEquals("test scenario", Factory.getGameRepository().getName());
            Assertions.assertEquals(80, Factory.getGameRepository().getHeight());
            Assertions.assertEquals(120, Factory.getGameRepository().getWidth());
            Assertions.assertEquals(0.1, Factory.getGameRepository().getScaling());
            Assertions.assertEquals(1, Factory.getGameRepository().getGuardCount());
            Assertions.assertEquals(0, Factory.getGameRepository().getIntruderCount());
            Assertions.assertEquals(14, Factory.getGameRepository().getBaseSpeedGuards());
            Assertions.assertEquals(14, Factory.getGameRepository().getBaseSpeedIntruders());
            Assertions.assertEquals(20, Factory.getGameRepository().getSpringSpeedIntruders());

            checkIfTargetAreaCorrect();
            checkIfGuardSpawnAreaCorrect();
            checkIfIntruderSpawnAreaCorrect();
            checkIfWallsBuildCorrectly(50, 0, 51, 20, 2, 21);
            checkIfWallsBuildCorrectly(0, 0, 1, 80, 2, 81);
            checkIfWallsBuildCorrectly(0, 79, 120, 80, 121, 2);
            checkIfWallsBuildCorrectly(119, 0, 120, 80, 2, 81);
            checkIfWallsBuildCorrectly(0, 0, 120, 1, 121, 2);

            checkIfTeleporterBuildCorrectly(20, 70, 25, 75, 90, 50, Angle.UP, 6, 6);

            checkIfShaded(10, 20, 20, 40, 11, 21, true);
            checkIfShaded(21, 20, 22, 40, 2, 21, false);
        } catch (IOException e) {
            Assertions.fail();
        }
    }

    void checkIfTeleporterBuildCorrectly(int x1, int y1, int x2, int y2, int destX, int destY, Angle direction, int expectedWidht, int expectedHeight) {
        // Test if the target area is imported properly
        TileArea board = Factory.getMapRepository().getBoardAsArea();
        HashMap<Integer, HashMap<Integer, Tile>> sourceArea = board.subset(x1, y1, x2, y2);
        Optional<Tile> destinationOptional = board.getByCoordinates(destX, destY);

        if (sourceArea.isEmpty() || destinationOptional.isEmpty()) {
            Assertions.fail();
        }

        Tile destination = destinationOptional.get();

        Assertions.assertEquals(expectedWidht, sourceArea.size());
        Assertions.assertEquals(expectedHeight, sourceArea.entrySet().stream().findFirst().get().getValue().size());

        Assertions.assertEquals(destX, destination.getX());
        Assertions.assertEquals(destY, destination.getY());

        Optional<Item> teleportItem = destination.getItems().stream().filter(item -> item instanceof Teleporter).findFirst();

        if (teleportItem.isEmpty()) {
            Assertions.fail();
        }

        Assertions.assertInstanceOf(Teleporter.class, teleportItem.get());
        Teleporter destinationTeleporter = (Teleporter) teleportItem.get();
        Assertions.assertEquals(direction, destinationTeleporter.getDirection());

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowentry : sourceArea.entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowentry.getValue().entrySet()) {
                Tile tile = colEntry.getValue();
                Optional<Item> source = tile.getItems().stream().filter(item -> item instanceof Teleporter).findFirst();

                if (source.isEmpty()) {
                    Assertions.fail();
                }

                Assertions.assertInstanceOf(Teleporter.class, source.get());
            }
        }
    }

    void checkIfShaded(int x1, int y1, int x2, int y2, int expectedWidth, int expectedHeight, boolean shaded) {
        // Test if the target area is imported properly
        TileArea board = Factory.getMapRepository().getBoardAsArea();
        HashMap<Integer, HashMap<Integer, Tile>> shadedArea = board.subset(x1, y1, x2, y2);

        if (shadedArea.isEmpty()) {
            Assertions.fail();
        }

        Assertions.assertEquals(expectedWidth, shadedArea.size());
        Assertions.assertEquals(expectedHeight, shadedArea.entrySet().stream().findFirst().get().getValue().size());

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowentry : shadedArea.entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowentry.getValue().entrySet()) {
                Tile tile = colEntry.getValue();
                if (shaded) {
                    Assertions.assertInstanceOf(ShadowTile.class, tile);
                } else {
                    Assertions.assertInstanceOf(Tile.class, tile);
                }
            }
        }
    }

    void checkIfWallsBuildCorrectly(int x1, int y1, int x2, int y2, int expectedWidth, int expectedHeight) {
        // Test if the target area is imported properly
        TileArea board = Factory.getMapRepository().getBoardAsArea();
        HashMap<Integer, HashMap<Integer, Tile>> wallArea = board.subset(x1, y1, x2, y2);

        if (wallArea.isEmpty()) {
            Assertions.fail();
        }

        Assertions.assertEquals(expectedWidth, wallArea.size());
        Assertions.assertEquals(expectedHeight, wallArea.entrySet().stream().findFirst().get().getValue().size());

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowentry : wallArea.entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowentry.getValue().entrySet()) {
                Tile tile = colEntry.getValue();
                Optional<Item> wallItem = tile.getItems().stream().filter(item -> item instanceof Wall).findFirst();

                if (wallItem.isEmpty()) {
                    Assertions.fail();
                }

                Assertions.assertInstanceOf(Wall.class, wallItem.get());
            }
        }
    }

    void checkIfTargetAreaCorrect() {
        // Test if the target area is imported properly
        TileArea targetArea = Factory.getMapRepository().getTargetArea();
        List<Tile> bounds = targetArea.getBounds();

        if (bounds.isEmpty()) {
            Assertions.fail();
        }

        // Top Left
        Assertions.assertEquals(20, bounds.get(0).getX());
        Assertions.assertEquals(40, bounds.get(0).getY());

        // Bottom Left
        Assertions.assertEquals(20, bounds.get(1).getX());
        Assertions.assertEquals(45, bounds.get(1).getY());

        // Top Right
        Assertions.assertEquals(25, bounds.get(2).getX());
        Assertions.assertEquals(40, bounds.get(2).getY());

        // Bottom Right
        Assertions.assertEquals(25, bounds.get(3).getX());
        Assertions.assertEquals(45, bounds.get(3).getY());
    }

    void checkIfGuardSpawnAreaCorrect() {
        // Test if the guard spawn area is imported properly
        TileArea targetArea = Factory.getMapRepository().getGuardSpawnArea();
        List<Tile> bounds = targetArea.getBounds();

        if (bounds.isEmpty()) {
            Assertions.fail();
        }

        // Top Left
        Assertions.assertEquals(2, bounds.get(0).getX());
        Assertions.assertEquals(2, bounds.get(0).getY());

        // Bottom Left
        Assertions.assertEquals(2, bounds.get(1).getX());
        Assertions.assertEquals(10, bounds.get(1).getY());

        // Top Right
        Assertions.assertEquals(20, bounds.get(2).getX());
        Assertions.assertEquals(2, bounds.get(2).getY());

        // Bottom Right
        Assertions.assertEquals(20, bounds.get(3).getX());
        Assertions.assertEquals(10, bounds.get(3).getY());
    }

    void checkIfIntruderSpawnAreaCorrect() {
        // Test if the intruder spawn area is imported properly
        TileArea targetArea = Factory.getMapRepository().getIntruderSpawnArea();
        List<Tile> bounds = targetArea.getBounds();

        if (bounds.isEmpty()) {
            Assertions.fail();
        }

        // Top Left
        Assertions.assertEquals(2, bounds.get(0).getX());
        Assertions.assertEquals(2, bounds.get(0).getY());

        // Bottom Left
        Assertions.assertEquals(2, bounds.get(1).getX());
        Assertions.assertEquals(10, bounds.get(1).getY());

        // Top Right
        Assertions.assertEquals(20, bounds.get(2).getX());
        Assertions.assertEquals(2, bounds.get(2).getY());

        // Bottom Right
        Assertions.assertEquals(20, bounds.get(3).getX());
        Assertions.assertEquals(10, bounds.get(3).getY());
    }
}
