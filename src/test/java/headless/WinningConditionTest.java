package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IMapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameMode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameState;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Intruder;
import com.nscharrenberg.um.multiagentsurveillance.headless.repositories.GameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.repositories.MapRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.repositories.PlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class WinningConditionTest {
    MapImporter mapImporter = new MapImporter();
    IGameRepository gameRepository;
    IMapRepository mapRepository;
    IPlayerRepository playerRepository;

    @BeforeEach
    public void setup() {
        gameRepository = new GameRepository();
        mapRepository = new MapRepository();
        playerRepository = new PlayerRepository();

        gameRepository.setMapRepository(mapRepository);
        gameRepository.setPlayerRepository(playerRepository);

        mapRepository.setPlayerRepository(playerRepository);
        mapRepository.setGameRepository(gameRepository);

        playerRepository.setGameRepository(gameRepository);
        playerRepository.setMapRepository(mapRepository);

        mapImporter = new MapImporter(gameRepository, mapRepository, playerRepository);

        mapImporter.parseLine("height = 50");
        mapImporter.parseLine("width = 50");
        mapImporter.parseLine("numGuards = 1");
        mapImporter.parseLine("numIntruders = 3");
        mapImporter.parseLine("baseSpeedGuard = 5");
        mapImporter.parseLine("distanceViewing = 10");
        mapImporter.parseLine("numberMarkers = 5");
        mapImporter.parseLine("smellingDistance = 5");
        mapImporter.parseLine("spawnAreaGuards = 1 1 5 5");
        mapImporter.parseLine("spawnAreaIntruders = 45 45 50 50");
        mapImporter.parseLine("targetArea = 48 48 49 49");
    }

    @DisplayName("Guard vs Intruders (All) - Intruders Win Test")
    @Test
    public void GuardIntruderAllIntruderWinTest() throws CollisionException, ItemAlreadyOnTileException, InvalidTileException, ItemNotOnTileException, BoardNotBuildException {
        mapImporter.parseLine("gameMode = 1");

        // Spawn Agents in fixed location
        Optional<Tile> guardOneTile = mapRepository.getBoardAsArea().getByCoordinates(1, 1);
        Optional<Tile> guardTwoTile = mapRepository.getBoardAsArea().getByCoordinates(1, 2);
        Optional<Tile> guardThreeTile = mapRepository.getBoardAsArea().getByCoordinates(1, 3);
        Optional<Tile> guardFourTile = mapRepository.getBoardAsArea().getByCoordinates(1, 4);
        Optional<Tile> guardFiveTile = mapRepository.getBoardAsArea().getByCoordinates(1, 5);
        Optional<Tile> intrudeOneTile = mapRepository.getBoardAsArea().getByCoordinates(50, 48);
        Optional<Tile> intrudeTwoTile = mapRepository.getBoardAsArea().getByCoordinates(50, 49);
        Optional<Tile> intrudeThreeTile = mapRepository.getBoardAsArea().getByCoordinates(50, 50);

        guardOneTile.ifPresent(tile -> playerRepository.spawn(Guard.class, tile));
        guardTwoTile.ifPresent(tile -> playerRepository.spawn(Guard.class, tile));
        guardThreeTile.ifPresent(tile -> playerRepository.spawn(Guard.class, tile));
        guardFourTile.ifPresent(tile -> playerRepository.spawn(Guard.class, tile));
        guardFiveTile.ifPresent(tile -> playerRepository.spawn(Guard.class, tile));

        intrudeOneTile.ifPresent(tile -> playerRepository.spawn(Intruder.class, tile));
        intrudeTwoTile.ifPresent(tile -> playerRepository.spawn(Intruder.class, tile));
        intrudeThreeTile.ifPresent(tile -> playerRepository.spawn(Intruder.class, tile));

        // start game (just boolean check)
        gameRepository.setRunning(true);

        /**
         * Agent 2 Movements & Checks
         */

        // Ensure everything is setup that we need for the test
        Assertions.assertEquals(3, playerRepository.getIntruders().size());
        Assertions.assertEquals(0, playerRepository.getEscapedIntruders().size());
        Assertions.assertEquals(0, playerRepository.getCaughtIntruders().size());

        playerRepository.move(playerRepository.getIntruders().get(0), Action.LEFT);
        playerRepository.move(playerRepository.getIntruders().get(0), Action.LEFT);
        playerRepository.move(playerRepository.getIntruders().get(0), Action.LEFT);
        playerRepository.move(playerRepository.getIntruders().get(0), Action.PLACE_MARKER_TARGET);
        playerRepository.move(playerRepository.getIntruders().get(0), Action.PLACE_MARKER_TARGET);
        playerRepository.move(playerRepository.getIntruders().get(0), Action.PLACE_MARKER_TARGET);

        Assertions.assertEquals(GameState.NO_RESULT, gameRepository.getGameState());
        Assertions.assertTrue(gameRepository.isRunning());

        Assertions.assertEquals(2, playerRepository.getIntruders().size());
        Assertions.assertEquals(1, playerRepository.getEscapedIntruders().size());
        Assertions.assertEquals(0, playerRepository.getCaughtIntruders().size());

        playerRepository.move(playerRepository.getIntruders().get(0), Action.LEFT);
        playerRepository.move(playerRepository.getIntruders().get(0), Action.LEFT);
        playerRepository.move(playerRepository.getIntruders().get(0), Action.LEFT);
        playerRepository.move(playerRepository.getIntruders().get(0), Action.PLACE_MARKER_TARGET);
        playerRepository.move(playerRepository.getIntruders().get(0), Action.PLACE_MARKER_TARGET);
        playerRepository.move(playerRepository.getIntruders().get(0), Action.PLACE_MARKER_TARGET);

        Assertions.assertEquals(GameState.NO_RESULT, gameRepository.getGameState());
        Assertions.assertTrue(gameRepository.isRunning());

        Assertions.assertEquals(1, playerRepository.getIntruders().size());
        Assertions.assertEquals(2, playerRepository.getEscapedIntruders().size());
        Assertions.assertEquals(0, playerRepository.getCaughtIntruders().size());


        playerRepository.move(playerRepository.getIntruders().get(0), Action.LEFT);
        playerRepository.move(playerRepository.getIntruders().get(0), Action.LEFT);
        playerRepository.move(playerRepository.getIntruders().get(0), Action.LEFT);
        playerRepository.move(playerRepository.getIntruders().get(0), Action.UP);
        playerRepository.move(playerRepository.getIntruders().get(0), Action.UP);
        playerRepository.move(playerRepository.getIntruders().get(0), Action.UP);
        playerRepository.move(playerRepository.getIntruders().get(0), Action.PLACE_MARKER_TARGET);
        playerRepository.move(playerRepository.getIntruders().get(0), Action.PLACE_MARKER_TARGET);
        playerRepository.move(playerRepository.getIntruders().get(0), Action.PLACE_MARKER_TARGET);

        Assertions.assertEquals(GameState.INTRUDERS_WON, gameRepository.getGameState());
        Assertions.assertFalse(gameRepository.isRunning());

        Assertions.assertEquals(0, playerRepository.getIntruders().size());
        Assertions.assertEquals(3, playerRepository.getEscapedIntruders().size());
        Assertions.assertEquals(0, playerRepository.getCaughtIntruders().size());
    }
}
