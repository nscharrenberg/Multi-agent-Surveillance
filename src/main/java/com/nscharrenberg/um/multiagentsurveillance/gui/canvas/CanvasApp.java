package com.nscharrenberg.um.multiagentsurveillance.gui.canvas;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CanvasApp extends Application {
    private GameView view;

    @Override
    public void start(Stage primaryStage) throws Exception {
        view = new GameView(primaryStage);

        Scene scene = new Scene(view, 800, 800);

//        scene.setOnKeyPressed(e -> {
//            switch (e.getCode()) {
//                case ESCAPE -> {
//                    try {
//                        stop();
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        });

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
        launch(args);
    }
}
