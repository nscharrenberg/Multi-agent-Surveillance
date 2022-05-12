package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Items.Item;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.CharacterAudio;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.Vision.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class AudioTest {

    @DisplayName("SoundWaves Test")
    @Test
    void testAudioTiles() throws ItemAlreadyOnTileException {

        try {
            Factory.reset();
            Factory.getGameRepository().setHeight(10);
            Factory.getGameRepository().setWidth(10);
            Factory.getMapRepository().buildEmptyBoard();
            Factory.getMapRepository().addWall(5,2);
            Factory.getMapRepository().addWall(5,4);
        } catch(Exception ignored) {

        }

        CharacterAudio CA = new CharacterAudio(3);
        Tile pos = new Tile(4, 4);

        TileArea map = Factory.getMapRepository().getBoard();
        CA.updateAudio(map, pos);

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : map.getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                Tile t = colEntry.getValue();
                for (Item i: t.getItems()) {
                    if( i instanceof SoundWave) {
                        System.out.println("Tile: " + t.getX() + "-" + t.getY() + " has soundwave: ");
                        System.out.println("Direction: " + ((SoundWave) i).getDirection());
                        System.out.println("Strength: " + ((SoundWave) i).getStrength());
                    }
                }
            }
        }


    }
}

