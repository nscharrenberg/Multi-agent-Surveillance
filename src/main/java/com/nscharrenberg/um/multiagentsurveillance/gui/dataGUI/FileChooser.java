package com.nscharrenberg.um.multiagentsurveillance.gui.dataGUI;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;


public class FileChooser extends Application {



    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("File Chooser");

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("Recorder"));

        Button button = new Button("Select Directory");
        button.setOnAction(e -> {
            File selectedDirectory = null;

            File parentDirectory = new File(System.getProperty("user.dir") + "\\Recorder");

            while(selectedDirectory == null){
                selectedDirectory = directoryChooser.showDialog(primaryStage);


                if(selectedDirectory == null)
                    continue;
                else if(selectedDirectory.getParent() == null)
                    selectedDirectory = null;
                else if(!selectedDirectory.getParent().equals(parentDirectory.getAbsolutePath()))
                    selectedDirectory = null;
            }

            System.out.println(selectedDirectory.getAbsolutePath());
            primaryStage.close();
            try {
                new PathData().start(primaryStage, selectedDirectory);
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

