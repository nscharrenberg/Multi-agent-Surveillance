package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameMode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.TileArea;
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

        CharacterVision cvup = new CharacterVision(4, Angle.UP);
        // TODO: insert map here
        TileArea map = new TileArea();

        /* Display tiles */
        ArrayList<Tile> tiles = cvup.getVision(map, new Tile(4,4, null));
        int expSize = tiles.size();
        for (Tile t:tiles) {
            System.out.println("Tile: " + t.getX() + " - " + t.getY());
        }


        // Assertions.assertEquals(9, cvup.getConeVision(new Tile(0,0,null)).size());

    }

}

