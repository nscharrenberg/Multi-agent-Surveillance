package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.exceptions.*;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.AdvancedAngle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

public class TargetDirectionTest {

    @DisplayName("UP")
    @Test
    public void test1(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(5, 10);
        Assertions.assertEquals("UP", computeTargetDirection(pos.getX(), pos.getY()).name());
    }

    @DisplayName("DOWN")
    @Test
    public void test2(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(5, 2);
        Assertions.assertEquals("DOWN", computeTargetDirection(pos.getX(), pos.getY()).name());
    }

    @DisplayName("LEFT")
    @Test
    public void test3(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(10, 4);
        Assertions.assertEquals("LEFT", computeTargetDirection(pos.getX(), pos.getY()).name());
    }

    @DisplayName("RIGHT")
    @Test
    public void test4(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(0, 2);
        Assertions.assertEquals("RIGHT", computeTargetDirection(pos.getX(), pos.getY()).name());
    }


    public Angle computeTargetDirection(int x1, int y1) {

        File file = new File("src/test/resources/maps/testmap6.txt");

        if (!file.exists()) {
            Assertions.fail("Resource not found");
        }

        String path = file.getAbsolutePath();

        MapImporter importer = new MapImporter();

        try {
            importer.load(path);

        } catch (IOException e) {
        Assertions.fail();
        }

        TileArea targetArea = Factory.getMapRepository().getTargetArea();
        List<Tile> bounds = targetArea.getBounds();

        if (bounds.isEmpty()) {
            throw new RuntimeException("no target position provided for the intruder");
        }

        // Top Left
        int topLeftX = bounds.get(0).getX();
        int topLeftY = bounds.get(0).getY();

        // Bottom Right
        int bottomRightX = bounds.get(3).getX();
        int bottomRightY = bounds.get(3).getY();

        Tile targetCentre = new Tile((topLeftX + bottomRightX)/2, (topLeftY + bottomRightY)/2);

        int x2 = targetCentre.getX();
        int y2 = targetCentre.getY();

        int x = x2 - x1;
        int y = y2 - y1;

        int absX = Math.abs(x);
        int absY = Math.abs(y);

        if(x == 0 && y == 0)
            throw new RuntimeException("Target position Error");


        if(x == 0){

            if(y > 0) return Angle.DOWN;
            else return Angle.UP;

        } else if(y == 0){

            if(x > 0) return Angle.RIGHT;
            else return Angle.LEFT;

        } else if(x > 0){

            if(y > 0) {
                if (absX > absY) return Angle.RIGHT;
                else return Angle.DOWN;
            } else {
                if(absX > absY) return Angle.RIGHT;
                else return Angle.UP;
            }

        } else {

            if(y > 0) {
                if (absX > absY) return Angle.LEFT;
                else return Angle.DOWN;
            } else {
                if(absX > absY) return Angle.LEFT;
                else return Angle.UP;
            }

        }
    }

}
