package com.nscharrenberg.um.multiagentsurveillance.gui.dataGUI;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class FileChooser extends Application {

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("File Chooser");

        File parentDirectory = new File(System.getProperty("user.dir") + "\\DataRecorder");
        parentDirectory.mkdir();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(parentDirectory);

        Button button = new Button("Select Directory");
        button.setOnAction(e -> {
            if (!parentDirectory.exists()) {
                Alert parentAlert = new Alert(Alert.AlertType.INFORMATION);
                parentAlert.setTitle("Must run Terminal Simulator first");
                parentAlert.setContentText("There are no simulations to be found. Please run the Terminal Simulator at least once!");
                parentAlert.show();
            }

            File selectedDirectory = directoryChooser.showDialog(primaryStage);

            if(selectedDirectory == null)
                return;
            else if(selectedDirectory.getParent() == null)
                return;
            else if(!selectedDirectory.getParent().equals(parentDirectory.getAbsolutePath()))
                return;

            System.out.println(selectedDirectory.getAbsolutePath());
            primaryStage.close();
            try {
                new DataSettings().start(primaryStage, selectedDirectory);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        });

        button.setAlignment(Pos.CENTER);
        VBox vBox = new VBox(button);
        vBox.setAlignment(Pos.CENTER);
        //HBox hBox = new HBox(button1, button2);
        Scene scene = new Scene(vBox, 200, 100);

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

