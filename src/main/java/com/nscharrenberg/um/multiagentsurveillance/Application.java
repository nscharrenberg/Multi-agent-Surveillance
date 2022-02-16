package com.nscharrenberg.um.multiagentsurveillance;

import com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers.MainGUI;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Application extends javafx.application.Application {


    @Override
    public void start(Stage stage) throws IOException {
        // Reset & Instantiate Factory Repositories
        Factory.reset();

        MainGUI mainGUI = new MainGUI();
        mainGUI.start(new Stage());


/*        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();*/
    }

    public static void main(String[] args) {
        launch();
    }
}
