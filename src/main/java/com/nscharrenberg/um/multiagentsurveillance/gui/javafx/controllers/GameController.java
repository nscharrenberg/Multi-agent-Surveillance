package com.nscharrenberg.um.multiagentsurveillance.gui.javafx.controllers;

import com.nscharrenberg.um.multiagentsurveillance.agents.IAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.random.RandomAgent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameController {

    private List<IAgent> agents = new ArrayList<>();
    private GameBoardGUI boardGUI;

    public GameController(){
        Factory.init();

        importMap();

        boardGUI = new GameBoardGUI();

        // I guess here we would first call the start method on the home-/main screen right?
        boardGUI.start(new Stage());

        setupAgents();

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

    private void importMap() {
        File file = new File("src/test/resources/maps/testmap.txt");
        String path = file.getAbsolutePath();
        MapImporter importer = new MapImporter();

        Factory.getGameRepository().setRunning(true);

        try {
            importer.load(path);
        } catch (IOException e) {
//            Factory.getGameRepository().setRunning(false);
        }
    }

    private void gameFinished() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game is Finished");
        alert.setHeaderText("Game Finished");
        String s =" gone over all steps";
        alert.setContentText(s);
        alert.show();
    }

    private void setupAgents() {
        for (int i = 0; i <= Factory.getGameRepository().getGuardCount(); i++) {
            Factory.getPlayerRepository().spawn(Guard.class);
        }

        // TODO: spawn Intruders
        for (Guard guard : Factory.getPlayerRepository().getGuards()) {
            agents.add(new RandomAgent(guard));
        }
    }

    private void gameLoop() {
        int stepCount = 0;

        Factory.getGameRepository().setRunning(true);
        while (Factory.getGameRepository().isRunning()) {
            for (IAgent agent : agents) {
                agent.execute();
            }

            boardGUI.updateGUI();

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // temp step count check
            stepCount++;
            if (stepCount >= 1000) {
                break;
            }
        }
    }



}
