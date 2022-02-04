package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

public class MapImporterTest {
    @DisplayName("Map Import Successful")
    @Test
    void testMapImportSuccessful() {
        File file = new File("src/test/resources/maps/testmap.txt");

        if (!file.exists()) {
            Assertions.fail("Resource not found");
        }

        String path = file.getAbsolutePath();

        System.out.println(path);

        try {
            MapImporter.load(path);
            Assertions.assertTrue(true);
        } catch (IOException e) {
            Assertions.fail();
        }
    }
}
