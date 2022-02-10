package com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers;

import javafx.application.Application ;
import javafx.geometry.Insets;
import javafx.scene.Group ;
import javafx.scene.Parent;
import javafx.scene.Scene ;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
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
                Rectangle tile = new Rectangle(10, 10);

                if (i == 0 || j == 0 || i == GRID_WIDTH-1 || j == GRID_HEIGHT-1) {
                    tile.setFill(Color.GRAY);
                    text.setStroke(Color.WHITE);
                }
                else tile.setFill(Color.BURLYWOOD);
                tile.setStroke(Color.BLACK);

                text.setFont(Font.font(8));
                //gameGrid.add(new StackPane(tile, text), i, j);
                gameGrid.add(new StackPane(tile), i, j);
            }
        }
        //gameGrid.setPadding(new Insets(10, 10, 10, 10));
        return gameGrid;
    }



    public static void main(String args[]) {
        launch(args);
    }
}