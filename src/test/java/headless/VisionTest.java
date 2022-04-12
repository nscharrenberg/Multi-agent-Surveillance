//package headless;
//
//import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
//import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;
//import com.nscharrenberg.um.multiagentsurveillance.headless.utils.Vision.CharacterVision;
//import com.nscharrenberg.um.multiagentsurveillance.headless.utils.Geometrics;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//
//public class VisionTest {
//
//    @DisplayName("Raw Vision Test")
//    @Test
//    void testRawVisionTiles() {
//
//        CharacterVision cvup = new CharacterVision(3, Angle.RIGHT);
//        Tile pos = new Tile(3,3);
//
//        int[][] correctTiles = new int[10][2];
//        int[][] collectedTiles = new int[10][2];
//
//        try {
//            Factory.reset();
//            Factory.getGameRepository().setHeight(10);
//            Factory.getGameRepository().setWidth(10);
//            Factory.getMapRepository().buildEmptyBoard();
//            Factory.getMapRepository().addWall(5,2);
//            Factory.getMapRepository().addWall(5,4);
//        } catch(Exception exc) {
//
//        }
//
//        TileArea map = Factory.getMapRepository().getBoard();
//        ArrayList<Tile> tiles = cvup.getVision(map, pos);
//
//        int k = 0;
//
//        for (Tile t:tiles) {
//            collectedTiles[k][0] = t.getX();
//            collectedTiles[k][1] = t.getY();
//            k++;
//        }
//
//        correctTiles[0][0] = 3; correctTiles[0][1] = 3;
//        correctTiles[1][0] = 6; correctTiles[1][1] = 6;
//        correctTiles[2][0] = 6; correctTiles[2][1] = 3;
//        correctTiles[3][0] = 6; correctTiles[3][1] = 0;
//        correctTiles[4][0] = 5; correctTiles[4][1] = 5;
//        correctTiles[5][0] = 5; correctTiles[5][1] = 3;
//        correctTiles[6][0] = 5; correctTiles[6][1] = 1;
//        correctTiles[7][0] = 4; correctTiles[7][1] = 4;
//        correctTiles[8][0] = 4; correctTiles[8][1] = 3;
//        correctTiles[9][0] = 4; correctTiles[9][1] = 2;
//
//        Assertions.assertArrayEquals(correctTiles, collectedTiles);
//
//    }
//}
//
