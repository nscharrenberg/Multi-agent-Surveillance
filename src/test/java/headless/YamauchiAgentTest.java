package headless;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.AStar.AStar;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.utils.QueueNode;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.YamauchiAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.BFS.BFS;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

public class YamauchiAgentTest {

    @DisplayName("BFS Test")
    @Test
    public void bfsTest() {
        File file = new File("src/test/resources/maps/testmap2.txt");

        if (!file.exists()) {
            Assertions.fail("Resource not found");
        }

        String path = file.getAbsolutePath();

        MapImporter importer = new MapImporter();

        try {
            importer.load(path);

            Optional<Tile> spawntileOpt = Factory.getMapRepository().getBoard().getByCoordinates(2,2);

            if (spawntileOpt.isEmpty()) {
                Assertions.fail();
            }

            Tile spawnTile = spawntileOpt.get();

            Guard guard = new Guard(spawnTile, Angle.RIGHT);
            YamauchiAgent agent = new YamauchiAgent(guard);

            HashMap<Integer, HashMap<Integer, Tile>> region = Factory.getMapRepository().getBoard().subset(0, 0, 10,10);

            if (region.isEmpty()) {
                Assertions.fail();
            }

            agent.addKnowledge(region);

            Optional<Tile> targetOpt = Factory.getMapRepository().getBoard().getByCoordinates(5, 2);

            if (targetOpt.isEmpty()) {
                Assertions.fail();
            }

            Tile target = targetOpt.get();

            Optional<QueueNode> planDataOpt = (new BFS()).execute(agent.getKnowledge(), agent.getPlayer(), target);

            if (planDataOpt.isEmpty()) {
                Assertions.fail();
            }

            QueueNode planData = planDataOpt.get();

            Assertions.assertEquals(21, planData.getDistance());
            Assertions.assertEquals(21, planData.getMoves().size());
        } catch (IOException e) {
            Assertions.fail();
        }
    }

    @DisplayName("AStar Test")
    @Test
    public void AStarTest() {
        File file = new File("src/test/resources/maps/testmap2.txt");

        if (!file.exists()) {
            Assertions.fail("Resource not found");
        }

        String path = file.getAbsolutePath();

        MapImporter importer = new MapImporter();

        try {
            importer.load(path);

            Optional<Tile> spawntileOpt = Factory.getMapRepository().getBoard().getByCoordinates(2,2);

            if (spawntileOpt.isEmpty()) {
                Assertions.fail();
            }

            Tile spawnTile = spawntileOpt.get();

            Guard guard = new Guard(spawnTile, Angle.RIGHT);
            YamauchiAgent agent = new YamauchiAgent(guard);

            HashMap<Integer, HashMap<Integer, Tile>> region = Factory.getMapRepository().getBoard().subset(0, 0, 10,10);

            if (region.isEmpty()) {
                Assertions.fail();
            }

            agent.addKnowledge(region);

            Optional<Tile> targetOpt = Factory.getMapRepository().getBoard().getByCoordinates(5, 2);

            if (targetOpt.isEmpty()) {
                Assertions.fail();
            }

            Tile target = targetOpt.get();

            Optional<QueueNode> planDataOpt = (new AStar()).execute(agent.getKnowledge(), agent.getPlayer(), target);

            if (planDataOpt.isEmpty()) {
                Assertions.fail();
            }

            QueueNode planData = planDataOpt.get();

            Assertions.assertEquals(21, planData.getDistance());
            Assertions.assertEquals(21, planData.getMoves().size());
        } catch (IOException e) {
            Assertions.fail();
        }
    }
}
