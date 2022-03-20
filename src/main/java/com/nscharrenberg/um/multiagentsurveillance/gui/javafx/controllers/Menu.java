package com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

public class Menu {
    private Stage stage;
    private static final int window_Length = 800;
    private static final int window_Breadth = 600;

    public Menu(){
        start();
    }

    public void start() {
        stage = new Stage();

        Label type = new Label("Choose the type of player");
        RadioButton button = new RadioButton("Player 1");
        button.setSelected(false);
        RadioButton button1 = new RadioButton("Player 2");

        Label choose = new Label("Select to choose the map file");
        RadioButton button2 = new RadioButton("Choose map file");
        button2.setSelected(false);

        Label choose2 = new Label("Select to enable experiment mode");
        RadioButton button3 = new RadioButton("Enable experiment menu");
        button3.setSelected(false);

        Button button4 = new Button("Continue");
        button4.setStyle("-fx-background-color: #b8995e;");

        button4.setOnAction(event ->{
            try {
                new GameController();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            stage.close();
        });


        VBox vbox = new VBox();
        vbox.getChildren().addAll(type, button, button1);
        vbox.getChildren().addAll(choose, button2, choose2, button3, button4);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        Scene scene = new Scene(vbox);
        stage.setMinWidth(window_Length);
        stage.setMinHeight(window_Breadth);
        stage.setScene(scene);
        stage.show();
    }

    public static String getStylesheet() {
        return HomeScreen.class.getResource("assets/style.css").toString();
    }

    public static void main(String[] args) {
        launch();
    }

}
