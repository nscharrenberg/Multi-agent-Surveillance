package headless;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Stack;

//import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
//import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;
//import com.nscharrenberg.um.multiagentsurveillance.headless.utils.CharacterVision;
//import com.nscharrenberg.um.multiagentsurveillance.headless.utils.Geometrics;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//
public class StackTest {

    @DisplayName("Raw Vision Test")
    @Test
    void testStackTiles() {

        Stack<Tile> tiles = new Stack<Tile>();
        tiles.add(new Tile(3, 3));
        tiles.add(new Tile(3, 4));
        tiles.add(new Tile(3, 5));
        tiles.add(new Tile(6, 6));
        tiles.add(new Tile(8, 8));

        Tile dum = new Tile(3,3);
        Tile dum2 = new Tile(3,8);
        Tile dum3 = new Tile(5,5);
        long exist = tiles.stream().filter(t -> (t.getX() == dum.getX() && t.getY() == dum.getY())).count();
        long exist2 = tiles.stream().filter(t -> (t.getX() == dum2.getX() && t.getY() == dum2.getY())).count();
        long exist3 = tiles.stream().filter(t -> (t.getX() == dum3.getX() && t.getY() == dum3.getY())).count();

        boolean match =  tiles.stream().noneMatch(t -> (t.getX() == dum.getX() && t.getY() == dum.getY()));
        System.out.println(match);

        //tiles.removeIf(t -> t.getX() == && t.getY() == )

        Tile testt = tiles.stream().filter(t -> (t.getX() == dum.getX() && t.getY() == dum.getY())).findFirst().get();
        System.out.println(exist +" - "+ exist2+ " - "+ exist3);
        System.out.println(testt.getX() + " - " + testt.getY());

    }

}
