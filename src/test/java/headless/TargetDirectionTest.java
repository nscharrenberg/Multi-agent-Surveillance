package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.TargetDirection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class TargetDirectionTest {

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


}

