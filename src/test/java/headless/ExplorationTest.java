package headless;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.TileArea;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ExplorationTest {
    @DisplayName("Exploration Progress Test")
    @Test
    void explorationProgressSuccessful() {
        // TODO: Review this because of new logic
//        Factory.init();
//        Tile tile1 = new Tile(1, 1);
//        Tile tile2 = new Tile(1, 2);
//        Tile tile3 = new Tile(1, 3);
//        Tile tile4 = new Tile(2, 1);
//        Tile tile5 = new Tile(2, 2);
//        Tile tile6 = new Tile(2, 3);
//
//        Factory.getMapRepository().getBoard().add(tile1, tile2, tile3, tile4, tile5, tile6);
//
//        TileArea targetArea = new TileArea();
//        targetArea.add(tile1);
//        targetArea.add(tile4);
//        targetArea.add(tile2);
//        targetArea.add(tile5);
//
//        Factory.getPlayerRepository().spawn(Guard.class, targetArea);
//        Factory.getPlayerRepository().spawn(Guard.class, targetArea);
//
//        Agent agent1 = Factory.getPlayerRepository().getAgents().get(0);
//        Agent agent2 = Factory.getPlayerRepository().getAgents().get(1);
//
//        Assertions.assertNotNull(agent1);
//        Assertions.assertNotNull(agent2);
//
//        agent1.getKnowledge().add(tile1);
//        agent2.getKnowledge().add(tile4);
//        agent1.getKnowledge().add(tile2);
//
//        float percentage = Factory.getPlayerRepository().calculateExplorationPercentage();
//
//        Assertions.assertEquals(50.0, percentage);

    }
}
