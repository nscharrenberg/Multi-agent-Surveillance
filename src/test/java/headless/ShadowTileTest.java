package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ShadowTileTest {


    @Test
    void shadowUpBoundsLevel1(){
        Tile tile = new Tile(10, 10);
        int xBound = 10;
        int yBound = 10;
        Assertions.assertTrue(checkShadowTile(tile, xBound, yBound, Action.UP));
    }

    @Test
    void shadowUpBoundsLevel2(){
        Tile tile = new Tile(30, 9);
        int xBound = 10;
        int yBound = 10;
        Assertions.assertTrue(checkShadowTile(tile, xBound, yBound, Action.UP));
    }

    @Test
    void shadowUpBoundsLevel3(){
        Tile tile = new Tile(30, 8);
        int xBound = 10;
        int yBound = 10;
        Assertions.assertFalse(checkShadowTile(tile, xBound, yBound, Action.UP));
    }

    @Test
    void shadowDownBoundsLevel1(){
        Tile tile = new Tile(32, 10);
        int xBound = 10;
        int yBound = 10;
        Assertions.assertTrue(checkShadowTile(tile, xBound, yBound, Action.DOWN));
    }

    @Test
    void shadowDownBoundsLevel2(){
        Tile tile = new Tile(10, 11);
        int xBound = 10;
        int yBound = 10;
        Assertions.assertTrue(checkShadowTile(tile, xBound, yBound, Action.DOWN));
    }

    @Test
    void shadowDownBoundsLevel3(){
        Tile tile = new Tile(10, 12);
        int xBound = 10;
        int yBound = 10;
        Assertions.assertFalse(checkShadowTile(tile, xBound, yBound, Action.DOWN));
    }

    @Test
    void shadowLeftBoundsLevel1(){
        Tile tile = new Tile(10, 54);
        int xBound = 10;
        int yBound = 10;
        Assertions.assertTrue(checkShadowTile(tile, xBound, yBound, Action.LEFT));
    }

    @Test
    void shadowLeftBoundsLevel2(){
        Tile tile = new Tile(9, 10);
        int xBound = 10;
        int yBound = 10;
        Assertions.assertTrue(checkShadowTile(tile, xBound, yBound, Action.LEFT));
    }

    @Test
    void shadowLeftBoundsLevel3(){
        Tile tile = new Tile(8, 65);
        int xBound = 10;
        int yBound = 10;
        Assertions.assertFalse(checkShadowTile(tile, xBound, yBound, Action.LEFT));
    }

    @Test
    void shadowRightBoundsLevel1(){
        Tile tile = new Tile(10, 10);
        int xBound = 10;
        int yBound = 10;
        Assertions.assertTrue(checkShadowTile(tile, xBound, yBound, Action.RIGHT));
    }

    @Test
    void shadowRightBoundsLevel2(){
        Tile tile = new Tile(11, 65);
        int xBound = 10;
        int yBound = 10;
        Assertions.assertTrue(checkShadowTile(tile, xBound, yBound, Action.RIGHT));
    }

    @Test
    void shadowRightBoundsLevel3(){
        Tile tile = new Tile(12, 65);
        int xBound = 10;
        int yBound = 10;
        Assertions.assertFalse(checkShadowTile(tile, xBound, yBound, Action.RIGHT));
    }




    private boolean checkShadowTile(Tile tile, int xBound, int yBound, Action direction){

        int shadowBound = 1;

        if(direction == Action.UP){

            return tile.getY() >= yBound - shadowBound;

        } else if(direction == Action.DOWN){

            return tile.getY() <= yBound + shadowBound;

        } else if(direction == Action.LEFT){

            return tile.getX() >= xBound - shadowBound;

        } else if(direction == Action.RIGHT){

            return tile.getX() <= xBound + shadowBound;

        }

        return false;
    }
}
