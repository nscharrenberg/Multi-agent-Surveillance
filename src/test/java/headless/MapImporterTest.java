package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

            // Test if Height & Width are imported properly
            Assertions.assertEquals(80, Factory.getGameRepository().getHeight());
            Assertions.assertEquals(120, Factory.getGameRepository().getWidth());

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
        } catch (IOException e) {
            Assertions.fail();
        }
    }
}
