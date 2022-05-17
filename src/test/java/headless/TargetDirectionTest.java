package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.AdvancedAngle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.TargetDirection;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;


public class TargetDirectionTest {

    // Test target direction in ANGLE
    @DisplayName("UP")
    @Test
    public void test1(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(5, 10);
        Assertions.assertEquals("UP", computeTargetDirectionTesting(pos.getX(), pos.getY()).name());
    }

    @DisplayName("DOWN")
    @Test
    public void test2(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(5, 2);
        Assertions.assertEquals("DOWN", computeTargetDirectionTesting(pos.getX(), pos.getY()).name());
    }

    @DisplayName("LEFT")
    @Test
    public void test3(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(10, 4);
        Assertions.assertEquals("LEFT", computeTargetDirectionTesting(pos.getX(), pos.getY()).name());
    }

    @DisplayName("RIGHT")
    @Test
    public void test4(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(0, 2);
        Assertions.assertEquals("RIGHT", computeTargetDirectionTesting(pos.getX(), pos.getY()).name());
    }

    // Test target direction in ADVANCED ANGLE

    @DisplayName("UP")
    @Test
    public void test5(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(5, 4);
        Assertions.assertEquals("UP", computeTargetDirectionInAdvancedAngleTesting(pos.getX(), pos.getY()).name());
    }

    @DisplayName("DOWN")
    @Test
    public void test6(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(5, 2);
        Assertions.assertEquals("DOWN", computeTargetDirectionInAdvancedAngleTesting(pos.getX(), pos.getY()).name());
    }

    @DisplayName("LEFT")
    @Test
    public void test7(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(6, 4);
        Assertions.assertEquals("LEFT", computeTargetDirectionInAdvancedAngleTesting(pos.getX(), pos.getY()).name());
    }

    @DisplayName("RIGHT")
    @Test
    public void test8(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(4, 4);
        Assertions.assertEquals("RIGHT", computeTargetDirectionInAdvancedAngleTesting(pos.getX(), pos.getY()).name());
    }

    @DisplayName("BOTTOM_LEFT")
    @Test
    public void test9(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(10, 2);
        Assertions.assertEquals("BOTTOM_LEFT", computeTargetDirectionInAdvancedAngleTesting(pos.getX(), pos.getY()).name());
    }

    @DisplayName("TOP_LEFT")
    @Test
    public void test10(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(10, 6);
        Assertions.assertEquals("TOP_LEFT", computeTargetDirectionInAdvancedAngleTesting(pos.getX(), pos.getY()).name());
    }

    @DisplayName("BOTTOM_RIGHT")
    @Test
    public void test11(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(2, 2);
        Assertions.assertEquals("BOTTOM_RIGHT", computeTargetDirectionInAdvancedAngleTesting(pos.getX(), pos.getY()).name());
    }

    @DisplayName("TOP_RIGHT")
    @Test
    public void test12(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(2, 10);
        Assertions.assertEquals("TOP_RIGHT", computeTargetDirectionInAdvancedAngleTesting(pos.getX(), pos.getY()).name());
    }

    public static Angle computeTargetDirectionTesting(int x1, int y1) {

        File file = new File("src/test/resources/maps/testmap6.txt");

        if (!file.exists()) {
            throw new RuntimeException("Resource not found");
        }

        String path = file.getAbsolutePath();

        MapImporter importer = new MapImporter();

        try {
            importer.load(path);

        } catch (IOException e) {
            throw new RuntimeException("Importer failed");
        }

        return TargetDirection.computeTargetDirection(x1, y1);

    }

    public static AdvancedAngle computeTargetDirectionInAdvancedAngleTesting(int x, int y) {

        File file = new File("src/test/resources/maps/testmap6.txt");

        if (!file.exists()) {
            throw new RuntimeException("Resource not found");
        }

        String path = file.getAbsolutePath();

        MapImporter importer = new MapImporter();

        try {
            importer.load(path);

        } catch (IOException e) {
            throw new RuntimeException("Importer failed");
        }

        return TargetDirection.computeTargetDirectionInAdvancedAngle(x, y);

    }
}
