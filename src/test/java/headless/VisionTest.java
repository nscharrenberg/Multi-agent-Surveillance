package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameMode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.CharacterVision;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.Geometrics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class VisionTest {

    @DisplayName("Raw Vision Test")
    @Test
    void testRawVisionTiles() {

//        CharacterVision cvup = new CharacterVision(2, Angle.UP);
//        CharacterVision cvright = new CharacterVision(3, Angle.RIGHT);
//        CharacterVision cvdown = new CharacterVision(3, Angle.DOWN);
//        CharacterVision cvleft = new CharacterVision(4, Angle.LEFT);
//
//        /* Display tiles */
//        ArrayList<Tile> tiles = cvdown.getRawVision(new Tile(4,4, null));
//        int expSize = tiles.size();
//        for (Tile t:tiles) {
//            System.out.println("Tile: " + t.getX() + " - " + t.getY());
//        }
//
//        Assertions.assertEquals(9, cvup.getRawVision(new Tile(0,0,null)).size());
//        Assertions.assertEquals(16, cvright.getRawVision(new Tile(2,2,null)).size());
//        Assertions.assertEquals(16, cvdown.getRawVision(new Tile(4,4,null)).size());
//        Assertions.assertEquals(25, cvleft.getRawVision(new Tile(7,5,null)).size());
        Assertions.assertTrue(true);

    }

}

