package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Teleporter;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Collision.Wall;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class MapBuildTest {
    private final int height = 50;
    private final int width = 50;

    @BeforeEach
    public void beforeEach() {
        Factory.reset();
        Factory.getGameRepository().setHeight(height);
        Factory.getGameRepository().setWidth(width);
        Factory.getMapRepository().buildEmptyBoard();
    }

    @DisplayName("Build Empty Map Success")
    @Test
    void testBuildMapEmptySuccess() {
        Assertions.assertEquals(height + 1, Factory.getMapRepository().getBoard().height());
        Assertions.assertEquals(width + 1, Factory.getMapRepository().getBoard().width());
    }

    @DisplayName("Add Wall Tile Success")
    @Test
    void testAddWallTileSuccess() {
        int x = 20;
        int y = 10;

        try {
            Factory.getMapRepository().addWall(x, y);

           Optional<Tile> found = Factory.getMapRepository().getBoardAsArea().getByCoordinates(x, y);

           if (found.isEmpty()) {
               Assertions.fail("Tile could not be found");
           }

           Tile tile = found.get();

           Assertions.assertEquals(x, tile.getX());
           Assertions.assertEquals(y, tile.getY());

           Item item = tile.getItems().get(0);

           Assertions.assertInstanceOf(Wall.class, item);
           Assertions.assertEquals(x, item.getTile().getX());
           Assertions.assertEquals(y, item.getTile().getY());
        } catch (InvalidTileException | BoardNotBuildException | ItemAlreadyOnTileException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @DisplayName("Add Wall Area Success")
    @Test
    void testAddWallAreaSuccess() {
        int x1 = 20;
        int y1 = 10;
        int x2 = 30;
        int y2 = 15;

        try {
            Factory.getMapRepository().addWall(x1, y1, x2, y2);

//            List<Tile> subset = Factory.getMapRepository().getBoardAsArea().subset(x1, y1, x2, y2);
//
//            if (subset.isEmpty()) {
//                Assertions.fail("Tiles could not be found");
//            }
//
//            Assertions.assertEquals((x2 - x1 + 1) * (y2 - y1 + 1), subset.size());
//
//            for (Tile tile : subset) {
//                Assertions.assertEquals(1, tile.getItems().size());
//
//                Item wall = tile.getItems().get(0);
//
//                Assertions.assertInstanceOf(Wall.class, wall);
//            }
        } catch (InvalidTileException | BoardNotBuildException | ItemAlreadyOnTileException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @DisplayName("Move Guard Success")
    @Test
    void testMoveGuardSuccessful() throws ItemAlreadyOnTileException, CollisionException, InvalidTileException, ItemNotOnTileException {
        int x = 20;
        int y = 10;

        Optional<Tile> tileOpt = Factory.getMapRepository().getBoardAsArea().getByCoordinates(x, y);

        if (tileOpt.isEmpty()) {
            Assertions.fail("Tiles could not be found");
        }

        Tile tile = tileOpt.get();

        Guard guard = new Guard(tile, Angle.UP);
        tile.add(guard);

        // Check if player exists
        Assertions.assertEquals(guard.getTile().getItems(), tile.getItems());
        Assertions.assertEquals(1, tile.getItems().size());
        Assertions.assertEquals(guard, tile.getItems().get(0));

        Angle move = Angle.DOWN;

        // Rotate the player
        Factory.getPlayerRepository().move(guard, move);

        Assertions.assertEquals(move, guard.getDirection());
        Assertions.assertEquals(move, ((Player) tile.getItems().get(0)).getDirection());

        // Move the player 1 tile down
        Factory.getPlayerRepository().move(guard, move);

        Assertions.assertEquals(move, guard.getDirection());
        Assertions.assertNotEquals(tile, guard.getTile());
        Assertions.assertEquals(0, tile.getItems().size());

        int nextX = x + move.getxIncrement();
        int nextY = y + move.getyIncrement();
        Optional<Tile> nextTileOpt = Factory.getMapRepository().getBoardAsArea().getByCoordinates(nextX, nextY);

        if (nextTileOpt.isEmpty()) {
            Assertions.fail("Tiles could not be found");
        }

        Tile nextTile = nextTileOpt.get();

        Assertions.assertEquals(1, nextTile.getItems().size());
        Assertions.assertEquals(guard, nextTile.getItems().get(0));
        Assertions.assertEquals(guard.getTile(), nextTile.getItems().get(0).getTile());
    }

    @DisplayName("Move Guard Success")
    @Test
    void testMoveGuardIntoWall() throws ItemAlreadyOnTileException, InvalidTileException, BoardNotBuildException, CollisionException, ItemNotOnTileException {
        int x = 20;
        int y = 10;

        Optional<Tile> tileOpt = Factory.getMapRepository().getBoardAsArea().getByCoordinates(x, y);

        if (tileOpt.isEmpty()) {
            Assertions.fail("Tiles could not be found");
        }

        Tile tile = tileOpt.get();

        Guard guard = new Guard(tile, Angle.UP);
        tile.add(guard);

        // Check if player exists
        Assertions.assertEquals(guard.getTile().getItems(), tile.getItems());
        Assertions.assertEquals(1, tile.getItems().size());
        Assertions.assertEquals(guard, tile.getItems().get(0));

        Angle move = Angle.UP;

        // build wall
        int wallX = tile.getX() + move.getxIncrement();
        int wallY = tile.getY() + move.getyIncrement();

        Factory.getMapRepository().addWall(wallX, wallY);

        // Move the player into the wall
        Assertions.assertThrows(CollisionException.class, () -> {
            Factory.getPlayerRepository().move(guard, move);
        });

        Assertions.assertEquals(move, guard.getDirection());
        Assertions.assertEquals(guard.getTile(), tile);
        Assertions.assertEquals(guard, tile.getItems().get(0));
        Assertions.assertEquals(move, ((Player) tile.getItems().get(0)).getDirection());
    }


    @DisplayName("Teleport Success")
    @Test
    void testTeleporter() throws ItemAlreadyOnTileException, InvalidTileException, BoardNotBuildException, CollisionException, ItemNotOnTileException {
        // Source
        int x1 = 5;
        int y1 = 2;
        int x2 = 6;
        int y2 = 3;

        Angle facingDirection = Angle.LEFT;

        // Destination
        int destX = 20;
        int destY = 10;

        Factory.getMapRepository().addTeleporter(x1, y1, x2, y2, destX, destY, facingDirection);

        // Player Spawn
        int x = 4;
        int y = 2;

        Optional<Tile> tileOpt = Factory.getMapRepository().getBoardAsArea().getByCoordinates(x, y);

        if (tileOpt.isEmpty()) {
            Assertions.fail("Tiles could not be found");
        }

        Tile tile = tileOpt.get();

        Guard guard = new Guard(tile, Angle.RIGHT);
        tile.add(guard);

        Angle move = Angle.RIGHT;

        // build wall
        int teleportSourceX = tile.getX() + move.getxIncrement();
        int teleportSourceY = tile.getY() + move.getyIncrement();

        Optional<Tile> nextTileOpt = Factory.getMapRepository().getBoardAsArea().getByCoordinates(teleportSourceX, teleportSourceY);

        if (nextTileOpt.isEmpty()) {
            Assertions.fail("Tiles could not be found");
        }

        Tile nextTile = nextTileOpt.get();

        Assertions.assertInstanceOf(Teleporter.class, nextTile.getItems().get(0));

        Factory.getPlayerRepository().move(guard, move);

        Optional<Tile> sourceTileOpt = Factory.getMapRepository().getBoardAsArea().getByCoordinates(destX, destY);

        if (sourceTileOpt.isEmpty()) {
            Assertions.fail("Tiles could not be found");
        }

        Tile sourceTile = sourceTileOpt.get();

        Assertions.assertEquals(sourceTile, guard.getTile());
        Assertions.assertNotEquals(nextTile, guard.getTile());
    }
}
