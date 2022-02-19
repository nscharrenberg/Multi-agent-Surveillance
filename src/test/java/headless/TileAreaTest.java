package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Area;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.TileArea;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TileAreaTest {

    @DisplayName("Merge Areas")
    @Test
    void mergeTestSuccessful() {
        Tile tile1 = new Tile(1, 1);
        Tile tile2 = new Tile(1, 2);
        Tile tile3 = new Tile(1, 3);
        Tile tile4 = new Tile(2, 1);
        Tile tile5 = new Tile(2, 2);
        Tile tile6 = new Tile(2, 3);

        TileArea area1 = new TileArea();
        TileArea area2 = new TileArea();

        area1.add(tile1, tile2, tile3);
        area2.add(tile4, tile5, tile6);

        Assertions.assertTrue(area1.within(tile1.getX(), tile1.getY()));
        Assertions.assertFalse(area1.within(tile4.getX(), tile4.getY()));
        Assertions.assertFalse(area2.within(tile1.getX(), tile1.getY()));
        Assertions.assertTrue(area2.within(tile4.getX(), tile4.getY()));

        Area<Tile> merged = area1.merge(area2);

        Assertions.assertTrue(merged.within(tile1.getX(), tile1.getY()));
        Assertions.assertTrue(merged.within(tile4.getX(), tile4.getY()));
    }
}
