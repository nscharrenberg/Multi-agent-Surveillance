package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;
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

    CharacterVision cvup = new CharacterVision(3, Angle.RIGHT);
        Tile pos = new Tile(0,0);

        try {
            Factory.reset();
            Factory.getGameRepository().setHeight(5);
            Factory.getGameRepository().setWidth(5);
            Factory.getMapRepository().buildEmptyBoard();
            Factory.getMapRepository().addWall(2,0);
        } catch(Exception exc) {

        }
        TileArea map = Factory.getMapRepository().getBoard();

        System.out.println(map.getByCoordinates(2,0).get().getItems().size());
        Item dummy = map.getByCoordinates(2,0).get().getItems().get(0);
        System.out.println(dummy.getTile());

         /* Display tiles */
        ArrayList<Tile> tiles = cvup.getVision(map, pos);
        for (Tile t:tiles) {
            System.out.println("Tile: " + t.getX() + " - " + t.getY());
        }

        // Assertions.assertEquals(9, cvup.getConeVision(new Tile(0,0,null)).size());

    }

}

