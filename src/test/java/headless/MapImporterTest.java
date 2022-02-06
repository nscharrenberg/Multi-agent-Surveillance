package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MapImporterTest {
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
            Assertions.assertEquals(3, Factory.getGameRepository().getGuardCount());
            Assertions.assertEquals(0, Factory.getGameRepository().getIntruderCount());
            Assertions.assertEquals(14, Factory.getGameRepository().getBaseSpeedGuards());
            Assertions.assertEquals(14, Factory.getGameRepository().getBaseSpeedIntruders());
            Assertions.assertEquals(20, Factory.getGameRepository().getSpringSpeedIntruders());

            checkIfTargetAreaCorrect();
            checkIfGuardSpawnAreaCorrect();
            checkIfIntruderSpawnAreaCorrect();
            checkIfWallsBuildCorrectly();
        } catch (IOException e) {
            Assertions.fail();
        }
    }

    void checkIfWallsBuildCorrectly() {
        // Test if the target area is imported properly
        TileArea board = Factory.getMapRepository().getBoardAsArea();
        List<Tile> wallArea = board.subset(50, 0, 51, 20);

        if (wallArea.isEmpty()) {
            Assertions.fail();
        }

        for (Tile tile : wallArea) {
            Optional<Item> wallItem = tile.getItems().stream().filter(item -> item instanceof Wall).findFirst();

            if (wallItem.isEmpty()) {
                Assertions.fail();
            }

            Assertions.assertInstanceOf(Wall.class, wallItem.get());
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
