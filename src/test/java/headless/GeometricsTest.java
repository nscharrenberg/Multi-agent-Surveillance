package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.Geometrics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class GeometricsTest {

    @DisplayName("Intersecting Tiles Test")
    @Test
    void testIntersectingTiles() {

        Geometrics gm = new Geometrics();
//        int tilecount = gm.getIntersectingTiles(new Tile(3,3,null),
//                new Tile(6,1,null)).size();
//        Assertions.assertEquals(4 , tilecount);

//        ArrayList<Tile> it = gm.getIntersectingTiles(
//                new Tile(3,3,null),
//                new Tile(4,0,null));
//
//        for (Tile t : it) {
//            System.out.println("X: " + t.getX() + " - Y:" + t.getY());
//        }

    }
}
