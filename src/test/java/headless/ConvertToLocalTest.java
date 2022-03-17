package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.CollisionException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.InvalidTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemAlreadyOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.ItemNotOnTileException;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.CharacterVision;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.Geometrics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConvertToLocalTest {

    @DisplayName("Converting from Global to Local coordinates")
    @Test
    void testConvertToLocal() {
        File file = new File("src/test/resources/maps/testmap.txt");
        String path = file.getAbsolutePath();
        MapImporter importer = new MapImporter();


        try {
            importer.load(path);
        } catch (IOException e) {
            e.printStackTrace();
            Factory.getGameRepository().setRunning(false);
        }

        TileArea tileArea = Factory.getMapRepository().getGuardSpawnArea();
        Factory.getPlayerRepository().spawn(Guard.class, tileArea);

        Guard guard = Factory.getPlayerRepository().getGuards().get(0);
        Tile spawnTile = Factory.getPlayerRepository().getSpawnPoint(guard);
        System.out.println(spawnTile.getX() + " " + spawnTile.getY());

        try {
            Factory.getPlayerRepository().move(guard, Angle.RIGHT);
            Factory.getPlayerRepository().move(guard, Angle.RIGHT);
            Factory.getPlayerRepository().move(guard, Angle.RIGHT);
            Factory.getPlayerRepository().move(guard, Angle.DOWN);
            Factory.getPlayerRepository().move(guard, Angle.DOWN);

            Assertions.assertEquals(spawnTile.getX() + 2, guard.getTile().getX());
            Assertions.assertEquals(spawnTile.getY() + 1, guard.getTile().getY());

            List<Tile> toBeConverted = new ArrayList<Tile>();
            toBeConverted.add(new Tile(guard.getTile().getX(), guard.getTile().getY()));
            HashMap<Integer, HashMap<Integer, Tile>> actualTile = Factory.getPlayerRepository().convertToLocalVision(guard, (new TileArea(toBeConverted)).getRegion());

//            System.out.println(actualTile.get.getX() + " " + actualTile.get(0).getY());
//            Assertions.assertEquals(2, actualTile.get(0).getX());
//            Assertions.assertEquals(1, actualTile.get(0).getY());
        } catch (CollisionException | ItemAlreadyOnTileException | InvalidTileException | ItemNotOnTileException e) {
            e.printStackTrace();
        }


    }

}
