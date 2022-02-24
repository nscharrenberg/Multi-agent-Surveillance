package com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers;

import com.nscharrenberg.um.multiagentsurveillance.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomeScreen extends Application {
    private static final int window_Length = 800;
    private static final int window_Breadth = 600;
    private Stage stage;

    public void start(Stage stage) {
        this.stage = stage;

        Button play = new Button("Play");

        play.setOnAction(e -> {
            new GameController();

            stage.close();
        });


        Label space1 = new Label(" ");
        Button howTo = new Button("How To Play");
        Label space2 = new Label(" ");
        Button quit = new Button("Quit");
        play.setMinSize(200, 50);
        howTo.setMinSize(200, 50);
        quit.setMinSize(200, 50);
        play.setStyle("-fx-background-color: #b8995e;");
        howTo.setStyle("-fx-background-color: #b8995e;");
        quit.setStyle("-fx-background-color: #b8995e;");
        VBox vbox = new VBox(play, space1, howTo,space2, quit);
        vbox.setDisable(false);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vbox);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setMinWidth(window_Length);
        stage.setMinHeight(window_Breadth);
        stage.show();
    }
    public static String getStylesheet() {
        return HomeScreen.class.getResource("assets/style.css").toString();
    }


    public static void main(String args[]) {
        launch(args);
    }
}

