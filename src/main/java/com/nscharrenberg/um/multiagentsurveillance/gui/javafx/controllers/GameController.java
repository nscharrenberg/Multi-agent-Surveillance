package com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class GameController {
    private GameBoardGUI boardGUI;

    public GameController(){
        Factory.init();

        Factory.getGameRepository().startGame();

        boardGUI = new GameBoardGUI();

        // I guess here we would first call the start method on the home-/main screen right?
        boardGUI.start(new Stage());

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                gameLoop();

                System.out.println(" Game Finished ");
                gameFinished();
                Factory.getGameRepository().setRunning(false);

                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void gameFinished() {
        Factory.getGameRepository().stopGame();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game is Finished");
        alert.setHeaderText("Game Finished");
        String s =" gone over all steps";
        alert.setContentText(s);
        alert.show();
    }

    private void gameLoop() {
        while (Factory.getGameRepository().isRunning()) {
            for (Agent agent : Factory.getPlayerRepository().getAgents()) {
                try {
                    agent.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Platform.runLater(() -> {
                boardGUI.updateGUI();
            });
        }
    }



}
