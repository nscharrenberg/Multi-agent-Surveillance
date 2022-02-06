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

        try {
            MapImporter.load(path);

            Assertions.assertEquals(80, Factory.getGameRepository().getHeight());
            Assertions.assertEquals(120, Factory.getGameRepository().getWidth());

            TileArea targetArea = Factory.getMapRepository().getTargetArea();
            List<Tile> bounds = targetArea.getBounds();

            if (bounds.isEmpty()) {
                Assertions.fail();
            }

            Assertions.assertEquals(bounds.get(0).getX(), 20);
            Assertions.assertEquals(bounds.get(0).getY(), 40);
        } catch (IOException e) {
            Assertions.fail();
        }
    }
}
