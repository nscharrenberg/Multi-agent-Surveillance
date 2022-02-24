/*package com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Door;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.ShadowTile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Wall;
import javafx.application.Application ;
import javafx.geometry.Insets;
import javafx.scene.Group ;
import javafx.scene.Parent;
import javafx.scene.Scene ;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage ;
import javafx.scene.text.Font ;
import javafx.scene.text.FontPosture ;
import javafx.scene.text.FontWeight ;
import javafx.scene.text.Text ;

public class TestGUI extends Application {

    private final int FRAME_WIDTH = 1300;
    private final int FRAME_HEIGHT = 650;
    private final int GRID_WIDTH;
    private final int GRID_HEIGHT;

    private TileComponents[] components = {TileComponents.TILE, TileComponents.WALL, WINDOW, }

    public enum TileComponents {
        WINDOW,
        WALL,
        SHADED,
        TILE
    }


    public TestGUI(){
        GRID_WIDTH = 80;
        GRID_HEIGHT = 50;
    }

    public TestGUI(int GRID_WIDTH, int GRID_HEIGHT){
        this.GRID_WIDTH = GRID_WIDTH;
        this.GRID_HEIGHT = GRID_HEIGHT;
    }

    @Override
    public void start(Stage st) {

        GridPane grid = createGrid();
        Group group = new Group(grid);
        Scene sc = new Scene(group, FRAME_WIDTH, FRAME_HEIGHT);

        st.setTitle(" Multi-Agent Surveillance ");
        st.setScene(sc);

        st.show();
    }

    public GridPane createGrid() {

        GridPane gameGrid = new GridPane();

        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {

                Text text = new Text(i + "," + j);
                Rectangle tile = new Rectangle(30, 30);

                {ItemType.SHADED, ItemType.Player, ItemType.TILE};

                if (i == 0 || j == 0 || i == GRID_WIDTH-1 || j == GRID_HEIGHT-1) {
                    tile.setFill(Color.GRAY);
                    text.setStroke(Color.WHITE);
                }
                else tile.setFill(Color.BURLYWOOD);
                tile.setStroke(Color.BLACK);

                text.setFont(Font.font(8));
                //gameGrid.add(new StackPane(tile, text), i, j);

                if (i == 10 && j == 10){
                    Polygon polygon = new Polygon();

                    //Adding coordinates to the polygon
                    polygon.getPoints().addAll(faceRIGHT);

                    gameGrid.add(new StackPane(tile, polygon), i, j);
                }
                else
                    gameGrid.add(new StackPane(tile), i, j);
            }
        }
        //gameGrid.setPadding(new Insets(10, 10, 10, 10));
        return gameGrid;
    }


    private Rectangle createTile(){

    }

    private Rectangle createTile(){
        ItemType.WALL.order;
    }

    private Double[] faceUP = {15.0, 5.0, 25.0, 25.0, 15.0, 20.0, 5.0, 25.0, 15.0, 5.0 };
    private Double[] faceDOWN = {15.0, 25.0, 5.0, 5.0, 15.0, 10.0, 25.0, 5.0, 15.0, 25.0};
    private Double[] faceLEFT = {5.0, 15.0, 25.0, 5.0, 20.0, 15.0, 25.0, 25.0, 5.0, 15.0};
    private Double[] faceRIGHT = {25.0, 15.0, 5.0, 5.0, 10.0, 15.0, 5.0, 25.0, 25.0, 15.0};

    public enum ItemType {
        TILE(1, Tile.class),
        SHADOW(2, ShadowTile.class),
        WALL(3, Wall.class),
        DOOR(4, Door.class);

        ItemType(int order, Class<?> instance) {
            this.order = order;
            this.instance = instance;
        }

        private int order;
        private Class<?> instance;

        public int getOrder() {
            return order;
        }

        public Class<?> getInstance() {
            return instance;
        }

        public boolean isType(Item item) {
            // TODO: CHeck if item class is the same type as the instance
        }
    }

    public static void main(String args[]) {
        launch(args);
    }
}

//https://www.baeldung.com/java-enum-values*/

