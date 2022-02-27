package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.Geometrics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GeometricsTest {

    @DisplayName("Intersecting Tiles Test")
    @Test
    void testIntersectingTiles() {

        Geometrics gm = new Geometrics();
        int[][] correctTiles = new int[9][2];
        int[][] collectedTiles = new int[9][2];

        correctTiles[0][0] = 2; correctTiles[0][1] = 1;
        correctTiles[1][0] = 3; correctTiles[1][1] = 2;
        correctTiles[2][0] = 4; correctTiles[2][1] = 2;
        correctTiles[3][0] = 5; correctTiles[3][1] = 3;
        correctTiles[4][0] = 6; correctTiles[4][1] = 3;
        correctTiles[5][0] = 7; correctTiles[5][1] = 3;
        correctTiles[6][0] = 8; correctTiles[6][1] = 4;
        correctTiles[7][0] = 9; correctTiles[7][1] = 4;
        correctTiles[8][0] = 10; correctTiles[8][1] = 5;

        int k = 0;
        for (Tile t:gm.getIntersectingTiles(new Tile(1,1), new Tile(11,5))) {
            collectedTiles[k][0] = t.getX();
            collectedTiles[k][1] = t.getY();
            k++;
        }

        Assertions.assertArrayEquals(correctTiles, collectedTiles);
    }
}
