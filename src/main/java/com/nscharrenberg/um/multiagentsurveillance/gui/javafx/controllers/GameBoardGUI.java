package com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers;

import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.*;
import javafx.application.Application ;
import javafx.scene.Group ;
import javafx.scene.Scene ;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage ;
import javafx.scene.text.Font ;

import java.util.*;

public class GameBoardGUI extends Application {

    private final int FRAME_WIDTH = 2000;
    private final int FRAME_HEIGHT = 1500;
    private final int GRID_WIDTH;
    private final int GRID_HEIGHT;
    private final int RECTANGLE_SIZE = 30;
    private Scene scene;
    private Stage stage;

    private ArrayList<TileComponents> components = new ArrayList<TileComponents>(Arrays.asList(TileComponents.SHADED, TileComponents.WALL, TileComponents.DOOR, TileComponents.WINDOW, TileComponents.TELEPORTER,
            TileComponents.GUARD, TileComponents.INTRUDER));

    public enum TileComponents {
        DOOR,
        SHADED,
        GUARD,
        INTRUDER,
        TELEPORTER,
        WALL,
        WINDOW;
    }

    // TODO: Might have to change if require differing grid size
    private Double[] faceUP_Intruder = {15.0, 5.0, 25.0, 25.0, 15.0, 20.0, 5.0, 25.0, 15.0, 5.0 };
    private Double[] faceDOWN_Intruder = {15.0, 25.0, 5.0, 5.0, 15.0, 10.0, 25.0, 5.0, 15.0, 25.0};
    private Double[] faceLEFT_Intruder = {5.0, 15.0, 25.0, 5.0, 20.0, 15.0, 25.0, 25.0, 5.0, 15.0};
    private Double[] faceRIGHT_Intruder = {25.0, 15.0, 5.0, 5.0, 10.0, 15.0, 5.0, 25.0, 25.0, 15.0};

    private Double[] faceUP_Guard = {15.0, 5.0, 5.0, 15.0, 5.0, 25.0, 25.0, 25.0, 25.0, 15.0, 15.0, 5.0};
    private Double[] faceDOWN_Guard = {15.0, 25.0, 5.0, 15.0, 5.0, 5.0, 25.0, 5.0, 25.0, 15.0, 15.0, 25.0};
    private Double[] faceLEFT_Guard = {5.0, 15.0, 15.0, 25.0, 25.0, 25.0, 25.0, 5.0, 15.0, 5.0, 5.0, 15.0};
    private Double[] faceRIGHT_Guard = {25.0, 15.0, 15.0, 5.0, 5.0, 5.0, 5.0, 25.0, 25.0, 25.0, 25.0, 15.0};

    public GameBoardGUI(){
        GRID_WIDTH = Factory.getGameRepository().getWidth();
        GRID_HEIGHT = Factory.getGameRepository().getHeight();
    }

    @Override
    public void start(Stage st) {

        TileArea board = Factory.getMapRepository().getBoardAsArea();

        GridPane grid = createBoard(board);
        Group group = new Group(grid);
        scene = new Scene(group, FRAME_WIDTH, FRAME_HEIGHT);

        this.stage = st;

        stage.setTitle(" Multi-Agent Surveillance ");
        stage.setScene(scene);

        stage.show();
    }

    public void updateGUI(){
        GridPane grid = createBoard(Factory.getMapRepository().getBoardAsArea());

        if (grid != null && scene != null) {
            stage.getScene().setRoot(grid);
        }
    }

    /**
     *
     *
     * @param board
     * @return
     */
    public GridPane createBoard(TileArea board) {

        GridPane gameGrid = new GridPane();
        Optional<Tile> optTile;
        Tile tile;
        Rectangle rectangle;
        StackPane stackPane = new StackPane();


        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {

                rectangle = new Rectangle(RECTANGLE_SIZE, RECTANGLE_SIZE);
                rectangle.setStroke(Color.BLACK);

                optTile = board.getByCoordinates(i,j);
                if (optTile.isEmpty())
                    System.out.println("Tile is Empty");

                else{
                    tile = optTile.get();
                    stackPane = createTile(tile);
                }


                //gameGrid.add(new StackPane(tile, text), i, j);
                gameGrid.add(stackPane, i, j);
            }
        }
        //gameGrid.setPadding(new Insets(10, 10, 10, 10));
        return gameGrid;
    }

    private StackPane createTile(Tile tile){

        Rectangle rectangle = new Rectangle(RECTANGLE_SIZE, RECTANGLE_SIZE);
        Polygon polygon = null;
        Player player;

        if (tile instanceof ShadowTile)
            rectangle.setFill(Color.GREY);
        else
            rectangle.setFill(Color.BURLYWOOD);

        ArrayList<Item> orderedList = orderList((ArrayList<Item>) tile.getItems());

        for (Item item : orderedList) {
            if (item instanceof Wall) {
                rectangle.setFill(Color.BLACK);
            } else if (item instanceof Window) {
                rectangle.setFill(Color.BLUE);
            } else if (item instanceof Door) {
                rectangle.setFill(Color.BROWN);
            } else if (item instanceof Guard) {
                player = (Player) item;
                polygon = createGuard(player.getDirection());
                polygon.setFill(Color.BLUE);
            } else if (item instanceof Intruder) {
                player = (Player) item;
                polygon = createIntruder(player.getDirection());
                polygon.setFill(Color.BLUE);
            }else if (item instanceof  Teleporter){
                rectangle.setFill(Color.PURPLE);
            }
        }

        rectangle.setStroke(Color.BLACK);

        if (polygon == null)
            return new StackPane(rectangle);
        else
            return new StackPane(rectangle, polygon);
    }

    private Polygon createGuard(Angle angle){

        Polygon polygon = new Polygon();

        if (angle == Angle.UP)
            polygon.getPoints().addAll(faceUP_Guard);
        else if (angle == Angle.DOWN)
            polygon.getPoints().addAll(faceDOWN_Guard);
        else if (angle == Angle.LEFT)
            polygon.getPoints().addAll(faceLEFT_Guard);
        else if (angle == Angle.RIGHT)
            polygon.getPoints().addAll(faceRIGHT_Guard);

        return polygon;
    }

    private Polygon createIntruder(Angle angle){

        Polygon polygon = new Polygon();

        if (angle == Angle.UP)
            polygon.getPoints().addAll(faceUP_Intruder);
        else if (angle == Angle.DOWN)
            polygon.getPoints().addAll(faceDOWN_Intruder);
        else if (angle == Angle.LEFT)
            polygon.getPoints().addAll(faceLEFT_Intruder);
        else if (angle == Angle.RIGHT)
            polygon.getPoints().addAll(faceRIGHT_Intruder);

        return polygon;

    }

    private ArrayList<Item> orderList(ArrayList<Item> itemList){

        ArrayList<Item> out = new ArrayList<>(Collections.nCopies(6, null));

        int index;

        for (Item item : itemList) {
            if (item instanceof Wall) {
                index = components.indexOf(TileComponents.WALL);
                out.set(index, item);
            } else if (item instanceof Window) {
                index = components.indexOf(TileComponents.WINDOW);
                out.set(index, item);
            } else if (item instanceof Door) {
                index = components.indexOf(TileComponents.DOOR);
                out.set(index, item);
            } else if (item instanceof Guard) {
                index = components.indexOf(TileComponents.GUARD);
                out.set(index, item);
            } else if (item instanceof Intruder) {
                index = components.indexOf(TileComponents.INTRUDER);
                out.set(index, item);
            }else if (item instanceof  Teleporter){
                index = components.indexOf(TileComponents.TELEPORTER);
                out.set(index, item);
            }
        }

        out.removeAll(Collections.singleton(null));

        return out;
    }

    /**
     * @param board
     * @return
     */
    public GridPane updateBoard(TileArea board) {
        //TODO: We need to store changes to the board somewhere, such that here we can take the original board and add the changes to it.

        return null;
    }


    public static void main(String args[]) {
        launch(args);
    }
}