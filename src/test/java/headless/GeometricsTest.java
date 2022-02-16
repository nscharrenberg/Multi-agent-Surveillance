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
        int[][] correctTiles = new int[11][2];
        int[][] collectedTiles = new int[11][2];

        correctTiles[0][0] = 1; correctTiles[0][1] = 1;
        correctTiles[1][0] = 2; correctTiles[1][1] = 1;
        correctTiles[2][0] = 3; correctTiles[2][1] = 2;
        correctTiles[3][0] = 4; correctTiles[3][1] = 2;
        correctTiles[4][0] = 5; correctTiles[4][1] = 3;
        correctTiles[5][0] = 6; correctTiles[5][1] = 3;
        correctTiles[6][0] = 7; correctTiles[6][1] = 3;
        correctTiles[7][0] = 8; correctTiles[7][1] = 4;
        correctTiles[8][0] = 9; correctTiles[8][1] = 4;
        correctTiles[9][0] = 10; correctTiles[9][1] = 5;
        correctTiles[10][0] = 11; correctTiles[10][1] = 5;

        int k = 0;
        for (Tile t:gm.getIntersectingTiles(new Tile(1,1,null), new Tile(11,5,null))) {
            collectedTiles[k][0] = t.getX();
            collectedTiles[k][1] = t.getY();
            k++;
        }

        Assertions.assertArrayEquals(correctTiles, collectedTiles);
    }
}
