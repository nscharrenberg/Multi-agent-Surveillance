package com.nscharrenberg.um.multiagentsurveillance.gui.canvas;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

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
                alert.setHeaderText("Legend");
                alert.setContentText(" FOREST GREEN = NORMAL TILE \n BROWN = WALL \n PURPLE = Teleport entrance \n LIGHT PURPLE = Teleport exit \n Black = Shadow \n Light Green = Knowledge \n Yellow/White = Vision \n Blue = Guard");
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
}
