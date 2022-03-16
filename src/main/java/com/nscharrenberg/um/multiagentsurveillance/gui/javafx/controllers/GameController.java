package com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class GameController {
    private GameBoardGUI boardGUI;
    private static int timeDelay = 300;

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
        Factory.getGameRepository().setRunning(true);
        while (Factory.getGameRepository().isRunning()) {
            for (Agent agent : Factory.getPlayerRepository().getAgents()) {
                try {
                    agent.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            boardGUI.updateGUI();

            try {
                Thread.sleep(timeDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}
