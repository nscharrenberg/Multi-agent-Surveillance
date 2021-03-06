package com.nscharrenberg.um.multiagentsurveillance;

import com.nscharrenberg.um.multiagentsurveillance.gui.canvas.CanvasApp;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        (new CanvasApp()).start(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}
