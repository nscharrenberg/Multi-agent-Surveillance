package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.BoardNotBuildException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Wall;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

public class MapBuildTest {
    private int height = 50;
    private int width = 50;

    @BeforeEach
    public void beforeEach() {
        Factory.reset();
        Factory.getGameRepository().setHeight(height);
        Factory.getGameRepository().setWidth(width);
    }

    @DisplayName("Build Empty Map Success")
    @Test
    void testBuildMapEmptySuccess() {
        Factory.getMapRepository().buildEmptyBoard();

        Assertions.assertEquals((height + 1) * (width + 1), Factory.getMapRepository().getBoard().size());
    }

    @DisplayName("Add Wall Tile Success")
    @Test
    void testAddWallTileSuccess() {
        Factory.getMapRepository().buildEmptyBoard();

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
        Factory.getMapRepository().buildEmptyBoard();

        int x1 = 20;
        int y1 = 10;
        int x2 = 30;
        int y2 = 15;

        try {
            Factory.getMapRepository().addWall(x1, y1, x2, y2);

            List<Tile> subset = Factory.getMapRepository().getBoardAsArea().subset(x1, y1, x2, y2);

            if (subset.isEmpty()) {
                Assertions.fail("Tiles could not be found");
            }

            Assertions.assertEquals((x2 - x1 + 1) * (y2 - y1 + 1), subset.size());

            for (Tile tile : subset) {
                Assertions.assertEquals(1, tile.getItems().size());

                Item wall = tile.getItems().get(0);

                Assertions.assertInstanceOf(Wall.class, wall);
            }
        } catch (InvalidTileException | BoardNotBuildException | ItemAlreadyOnTileException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }
}
