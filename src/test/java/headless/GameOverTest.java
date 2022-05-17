package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.utils.GameOver;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;


public class GameOverTest {

    @DisplayName("Test all winning conditions")
    @Test
    public void winningConditionTest() {
        File file = new File("src/test/resources/maps/testmap6.txt");

        if (!file.exists()) {
            Assertions.fail("Resource not found");
        }

        String path = file.getAbsolutePath();

        MapImporter importer = new MapImporter();

        try
        {
            importer.load(path);

            Assertions.assertFalse(GameOver.getGameMode());
            Assertions.assertEquals("GAME_IN_PROGRESS", GameOver.findIntruder().name());
            Assertions.assertEquals("GAME_IN_PROGRESS", GameOver.checkTargetArea().name());
            Assertions.assertEquals(1, GameOver.getIntruderNumber());
            Assertions.assertEquals(1, GameOver.getCaughtNumber());
            Assertions.assertEquals(0, GameOver.getEscapeNumber());
        }
        catch (IOException e)
        {
            Assertions.fail();
        }

    }

}
