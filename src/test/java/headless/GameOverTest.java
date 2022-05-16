package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.GameOver;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

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

        try {

            Factory.init();
            Tile tile1 = new Tile(1, 1);
            Tile tile2 = new Tile(1, 2);
            Tile tile3 = new Tile(1, 3);
            Tile tile4 = new Tile(2, 1);
            Tile tile5 = new Tile(2, 2);
            Tile tile6 = new Tile(2, 3);

            Factory.getMapRepository().getBoard().add(tile1, tile2, tile3, tile4, tile5, tile6);

            Optional<Tile> spawntileOpt = Factory.getMapRepository().getBoard().getByCoordinates(2, 2);
            importer.load(path);
            TileArea board = Factory.getMapRepository().getBoard();

            Tile IntruderPos = new Tile(1, 1);

            if (spawntileOpt.isEmpty()) {
                Assertions.fail();
            }

            Tile spawnTile = spawntileOpt.get();

            Intruder intruderPos = new Intruder(spawnTile, Angle.RIGHT);

            Optional<Tile> targetOpt = Factory.getMapRepository().getBoard().getByCoordinates(1, 2);

            if (targetOpt.isEmpty()) {
                Assertions.fail();
            }

            Tile target = targetOpt.get();

            Assertions.assertTrue(GameOver.checkGameMode());
            Assertions.assertEquals("LOSE", GameOver.findIntruder(board, IntruderPos).name());
            Assertions.assertEquals("LOSE", GameOver.checkTargetArea(board, intruderPos, target).name());
            Assertions.assertEquals(1, GameOver.getIntruderNumber());
            Assertions.assertEquals(1, GameOver.getCaughtNumber());
            Assertions.assertEquals(0, GameOver.getEscapeNumber());

        } catch (IOException e) {
            Assertions.fail();
        }

    }


}