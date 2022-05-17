package headless;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameMode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.GameOver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;


public class GameOverTest
{

    @DisplayName("Test all winning conditions")
    @Test
    public void testWinningConditions() throws InvalidTileException, ItemNotOnTileException {

        Factory.init();
        Factory.getGameRepository().setGameMode(GameMode.GUARD_INTRUDER);

        Tile tile1 = new Tile(1, 1);
        Tile tile2 = new Tile(1, 2);
        Tile tile3 = new Tile(1, 3);
        Tile tile4 = new Tile(2, 1);
        Tile tile5 = new Tile(2, 2);
        Tile tile6 = new Tile(2, 3);
        Tile tile7 = new Tile(3, 1);
        Tile tile8 = new Tile(3, 2);
        Tile tile9 = new Tile(3, 3);
        Tile tile10 = new Tile(4, 1);
        Tile tile11 = new Tile(4, 2);
        Tile tile12 = new Tile(4, 3);

        Factory.getMapRepository().getBoard().add(tile1, tile2, tile3, tile4, tile5, tile6, tile7, tile8, tile9, tile10, tile11, tile12);

        TileArea targetArea = new TileArea();
        targetArea.add(tile1);
        targetArea.add(tile4);
        targetArea.add(tile2);
        targetArea.add(tile5);

        Factory.getMapRepository().setTargetArea(targetArea);

        Factory.getPlayerRepository().spawn(Guard.class, targetArea);
        Factory.getPlayerRepository().spawn(Intruder.class, targetArea);

        Agent agent1 = Factory.getPlayerRepository().getAgents().get(0);
        Agent agent2 = Factory.getPlayerRepository().getAgents().get(1);

        Assertions.assertNotNull(agent1);
        Assertions.assertNotNull(agent2);

        agent1.getKnowledge().add(tile1);
        agent2.getKnowledge().add(tile4);
        agent1.getKnowledge().add(tile2);

        try {
            Assertions.assertTrue(GameOver.checkGameMode());
            Assertions.assertEquals("GAME_IN_PROCESS", GameOver.findIntruder().name());
            Assertions.assertEquals("GAME_IN_PROCESS", GameOver.checkTargetArea().name());
            Assertions.assertEquals(1, GameOver.getIntruderNumber());
            Assertions.assertEquals(0, GameOver.getCaughtNumber());
            Assertions.assertEquals(0, GameOver.getEscapeNumber());
        }catch(BoardNotBuildException ignored){

        }
    }
}