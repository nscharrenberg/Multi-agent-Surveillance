package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.GameMode;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ComputeDistanceBetweenTest {

    @DisplayName("+X1 +Y1 +X2 +Y2")
    @Test
    public void test1(){
        Tile x = new Tile(12, 23);
        Tile y = new Tile(32, 13);
        Assertions.assertEquals(30, computeDistanceBetween(x, y));
    }

    @DisplayName("-X1 +Y1 +X2 +Y2")
    @Test
    public void test2(){
        Tile x = new Tile(-34, 23);
        Tile y = new Tile(98, 34);
        Assertions.assertEquals(143, computeDistanceBetween(x, y));
    }

    @DisplayName("-X1 -Y1 +X2 +Y2")
    @Test
    public void test3(){
        Tile x = new Tile(-56, -83);
        Tile y = new Tile(83, 19);
        Assertions.assertEquals(241, computeDistanceBetween(x, y));
    }

    @DisplayName("-X1 -Y1 -X2 +Y2")
    @Test
    public void test4(){
        Tile x = new Tile(-45, -84);
        Tile y = new Tile(-29, 34);
        Assertions.assertEquals(134, computeDistanceBetween(x, y));
    }

    @DisplayName("-X1 -Y1 -X2 -Y2")
    @Test
    public void test5(){
        Tile x = new Tile(-32, -12);
        Tile y = new Tile(-47, -6);
        Assertions.assertEquals(21, computeDistanceBetween(x, y));
    }

    @DisplayName("+X1 -Y1 +X2 +Y2")
    @Test
    public void test6(){
        Tile x = new Tile(23, -56);
        Tile y = new Tile(35, 16);
        Assertions.assertEquals(84, computeDistanceBetween(x, y));
    }

    @DisplayName("+X1 -Y1 -X2 +Y2")
    @Test
    public void test7(){
        Tile x = new Tile(45, -32);
        Tile y = new Tile(-34, 24);
        Assertions.assertEquals(135, computeDistanceBetween(x, y));
    }

    @DisplayName("+X1 -Y1 -X2 -Y2")
    @Test
    public void test8(){
        Tile x = new Tile(56, -45);
        Tile y = new Tile(-20, -1);
        Assertions.assertEquals(120, computeDistanceBetween(x, y));
    }

    @DisplayName("+X1 +Y1 -X2 +Y2")
    @Test
    public void test9(){
        Tile x = new Tile(35, 10);
        Tile y = new Tile(-64, 10);
        Assertions.assertEquals(99, computeDistanceBetween(x, y));
    }

    @DisplayName("+X1 +Y1 -X2 -Y2")
    @Test
    public void test10(){
        Tile x = new Tile(34, 81);
        Tile y = new Tile(-41, -73);
        Assertions.assertEquals(229, computeDistanceBetween(x, y));
    }

    @DisplayName("+X1 +Y1 +X2 -Y2")
    @Test
    public void test11(){
        Tile x = new Tile(24, 81);
        Tile y = new Tile(56, -12);
        Assertions.assertEquals(125, computeDistanceBetween(x, y));
    }

    @DisplayName("-X1 +Y1 +X2 -Y2")
    @Test
    public void test12(){
        Tile x = new Tile(-24, 41);
        Tile y = new Tile(37, -77);
        Assertions.assertEquals(179, computeDistanceBetween(x, y));
    }

    @DisplayName("+X1 -Y1 +X2 -Y2")
    @Test
    public void test13(){
        Tile x = new Tile(88, -11);
        Tile y = new Tile(44, -56);
        Assertions.assertEquals(89, computeDistanceBetween(x, y));
    }

    @DisplayName("-X1 +Y1 -X2 +Y2")
    @Test
    public void test14(){
        Tile x = new Tile(-35, 91);
        Tile y = new Tile(-71, 41);
        Assertions.assertEquals(86, computeDistanceBetween(x, y));
    }

    public int computeDistanceBetween(Tile tileX, Tile tileY){
        int x = Math.abs(tileX.getX() - tileY.getX());
        int y = Math.abs(tileX.getY() - tileY.getY());
        return x + y;
    }
}
