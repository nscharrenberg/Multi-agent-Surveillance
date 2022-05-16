package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.TargetDirection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class TargetDirectionTest {

    // Test target direction in ANGLE
    @DisplayName("UP")
    @Test
    public void test1(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(5, 10);
        Assertions.assertEquals("UP", TargetDirection.computeTargetDirectionTesting(pos.getX(), pos.getY()).name());
    }

    @DisplayName("DOWN")
    @Test
    public void test2(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(5, 2);
        Assertions.assertEquals("DOWN", TargetDirection.computeTargetDirectionTesting(pos.getX(), pos.getY()).name());
    }

    @DisplayName("LEFT")
    @Test
    public void test3(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(10, 4);
        Assertions.assertEquals("LEFT", TargetDirection.computeTargetDirectionTesting(pos.getX(), pos.getY()).name());
    }

    @DisplayName("RIGHT")
    @Test
    public void test4(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(0, 2);
        Assertions.assertEquals("RIGHT", TargetDirection.computeTargetDirectionTesting(pos.getX(), pos.getY()).name());
    }

    // Test target direction in ADVANCED ANGLE

    @DisplayName("UP")
    @Test
    public void test5(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(5, 4);
        Assertions.assertEquals("UP", TargetDirection.computeTargetDirectionInAdvancedAngleTesting(pos.getX(), pos.getY()).name());
    }

    @DisplayName("DOWN")
    @Test
    public void test6(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(5, 2);
        Assertions.assertEquals("DOWN", TargetDirection.computeTargetDirectionInAdvancedAngleTesting(pos.getX(), pos.getY()).name());
    }

    @DisplayName("LEFT")
    @Test
    public void test7(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(6, 4);
        Assertions.assertEquals("LEFT", TargetDirection.computeTargetDirectionInAdvancedAngleTesting(pos.getX(), pos.getY()).name());
    }

    @DisplayName("RIGHT")
    @Test
    public void test8(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(4, 4);
        Assertions.assertEquals("RIGHT", TargetDirection.computeTargetDirectionInAdvancedAngleTesting(pos.getX(), pos.getY()).name());
    }

    @DisplayName("BOTTOM_LEFT")
    @Test
    public void test9(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(10, 2);
        Assertions.assertEquals("BOTTOM_LEFT", TargetDirection.computeTargetDirectionInAdvancedAngleTesting(pos.getX(), pos.getY()).name());
    }

    @DisplayName("TOP_LEFT")
    @Test
    public void test10(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(10, 6);
        Assertions.assertEquals("TOP_LEFT", TargetDirection.computeTargetDirectionInAdvancedAngleTesting(pos.getX(), pos.getY()).name());
    }

    @DisplayName("BOTTOM_RIGHT")
    @Test
    public void test11(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(2, 2);
        Assertions.assertEquals("BOTTOM_RIGHT", TargetDirection.computeTargetDirectionInAdvancedAngleTesting(pos.getX(), pos.getY()).name());
    }

    @DisplayName("TOP_RIGHT")
    @Test
    public void test12(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(2, 10);
        Assertions.assertEquals("TOP_RIGHT", TargetDirection.computeTargetDirectionInAdvancedAngleTesting(pos.getX(), pos.getY()).name());
    }

}

