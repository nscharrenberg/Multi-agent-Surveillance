package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameMode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.CharacterVision;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.Geometrics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class VisionTest {

    @DisplayName("Raw Vision Test")
    @Test
    void testRawVisionTiles() {

        CharacterVision cvup = new CharacterVision(4, Angle.UP);
        Tile pos = new Tile(1,1);

        Factory.reset();
        Factory.getGameRepository().setHeight(5);
        Factory.getGameRepository().setWidth(5);
        Factory.getMapRepository().buildEmptyBoard();
        TileArea map = Factory.getMapRepository().getBoard();

         /* Display tiles */
        ArrayList<Tile> tiles = cvup.getVision(map, new Tile(1,1));
        for (Tile t:tiles) {
            System.out.println("Tile: " + t.getX() + " - " + t.getY());
        }

        // Assertions.assertEquals(9, cvup.getConeVision(new Tile(0,0,null)).size());

    }

}

