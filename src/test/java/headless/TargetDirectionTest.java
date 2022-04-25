package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.AdvancedAngle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Stack;

public class TargetDirectionTest {

    @DisplayName("UP")
    @Test
    public void test1(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(22, 80);
        Assertions.assertEquals("UP", targetDirection(pos).name());
    }

    @DisplayName("DOWN")
    @Test
    public void test2(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(22, -50);
        Assertions.assertEquals("DOWN", targetDirection(pos).name());
    }

    @DisplayName("LEFT")
    @Test
    public void test3(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(50, 42);
        Assertions.assertEquals("LEFT", targetDirection(pos).name());
    }

    @DisplayName("RIGHT")
    @Test
    public void test4(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(-45, 42);
        Assertions.assertEquals("RIGHT", targetDirection(pos).name());
    }

    @DisplayName("TOP_LEFT")
    @Test
    public void test5(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(75, 75);
        Assertions.assertEquals("TOP_LEFT", targetDirection(pos).name());
    }

    @DisplayName("TOP_RIGHT")
    @Test
    public void test6(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(-20, 75);
        Assertions.assertEquals("TOP_RIGHT", targetDirection(pos).name());
    }

    @DisplayName("BOTTOM_LEFT")
    @Test
    public void test7(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(50, -30);
        Assertions.assertEquals("BOTTOM_LEFT", targetDirection(pos).name());
    }

    @DisplayName("BOTTOM_RIGHT")
    @Test
    public void test8(){
        // Test if the target direction is computed correctly
        Tile pos = new Tile(50, -40);
        Assertions.assertEquals("BOTTOM_LEFT", targetDirection(pos).name());
    }

    public AdvancedAngle targetDirection(Tile position)
    {
        if (position == null) {
            throw new RuntimeException("no intruder position provided for intruder");
        }
        
        // Upper Left
        int upperLeftX = 20;
        int upperLeftY = 40;

        // Bottom Left
        int bottomLeftX = 20;
        int bottomLeftY = 45;

        // Upper Right
        int upperRightX = 25;
        int upperRightY = 40;

        // Bottom Right
        int bottomRightX = 25;
        int bottomRightY = 45;

        Tile targetCentre = new Tile((upperLeftX + bottomRightX)/2, (upperLeftY + bottomRightY)/2);

        int dX = targetCentre.getX() - position.getX();
        int dY = targetCentre.getY() - position.getY();

        // Normalization
        dX = Integer.compare(dX, 0);
        dY = Integer.compare(dY, 0);

        int pick;
        if(dX == 0 && dY == -1)
            pick = 0;
        else if(dX == 0 && dY == 1)
            pick = 1;
        else if(dX == -1 && dY ==0)
            pick = 2;
        else if(dX == 1 && dY == 0)
            pick = 3;
        else if(dX == -1 && dY == -1)
            pick = 4;
        else if(dX == 1 && dY == -1)
            pick = 5;
        else if(dX == -1 && dY == 1)
            pick = 6;
        else
            pick = 7;

        AdvancedAngle targetDirection = AdvancedAngle.values()[pick];

        if(targetDirection == null)
        {
            throw new RuntimeException("no targetDirection provided for intruder");
        }

        return targetDirection;
    }
}