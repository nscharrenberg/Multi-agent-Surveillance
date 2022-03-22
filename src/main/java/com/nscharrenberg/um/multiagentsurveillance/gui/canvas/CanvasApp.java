package com.nscharrenberg.um.multiagentsurveillance.gui.canvas;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.HashMap;

public class CanvasApp extends Application {
    private GameView view;
    private static int WIDTH = 800;
    private static int HEIGHT = 800;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);
        view = new GameView(primaryStage);

        Scene scene = new Scene(view, 800, 800);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.L) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Legend");
                alert.setHeaderText("");
                //alert.setContentText(" FOREST GREEN = NORMAL TILE \n BROWN = WALL \n PURPLE = Teleport entrance \n LIGHT PURPLE = Teleport exit \n Black = Shadow \n Light Green = Knowledge \n Yellow/White = Vision \n Blue = Guard");
                alert.setGraphic(createLegend());
                alert.show();
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private GridPane createLegend(){
        int size = 20;
        GridPane legend = new GridPane();

        HashMap<String, Color> legendItems = GameView.getMapColours();
        Color color;
        Polygon polygon;

        int row = 0;
        for (String item : legendItems.keySet()) {
            color = legendItems.get(item);

            if (item == "Guard" || item == "Intruder") {
                polygon = createAgent(item, color, size);
                legend.add(polygon, 0, row);
                GridPane.setHalignment(polygon, HPos.CENTER);
            }
            else
                legend.add(createTile(color,size), 0, row);

            legend.add(new Label("= " + item), 1, row++);
        }

        legend.setVgap(2);
        legend.setHgap(10);

        return legend;
    }

    private Rectangle createTile(Color color, int size){
        Rectangle out = new Rectangle(size,size);
        out.setFill(color);
        return out;
    }

    private Polygon createAgent(String name, Color color, int size){
        Polygon agent = new Polygon();
        agent.setFill(color);

        if (name == "Guard")
            agent.getPoints().addAll(createGuardLegend(size));
        else
            agent.getPoints().addAll(createIntruderLegend(size));

        return agent;
    }

    private Double[] createGuardLegend(double size){
        return new Double[]{size / 2.0, size / 6.0, size / 6.0, size / 2.0, size / 6.0, size * 5.0 / 6.0, size * 5.0 / 6.0, size * 5.0 / 6.0, size * 5.0 / 6.0, size / 2.0, size / 2.0, size / 6.0};
    }

    private Double[] createIntruderLegend(double size){
        return new Double[]{size / 6.0, size * 5.0 / 6.0, size / 2.0, size / 6.0, size * 5.0 / 6.0, size * 5.0 / 6.0, size / 2.0, size * 2.0 / 3.0, size / 6.0, size * 5.0 / 6.0};
    }
}
