package com.nscharrenberg.um.multiagentsurveillance.gui.terminal;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.repositories.PlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.GameConfigurationRecorder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.RecordHelper.GAME_ID;

public class SimulatorRecorder {

    public SimulatorRecorder() throws Exception {
        Factory.init();
        importMap();
        spawn();
        new GameConfigurationRecorder().setUpConfFiles();
        gameLoop();
    }

    private void importMap() {
        File file = new File("src/test/resources/maps/testmap4.txt");
        String path = file.getAbsolutePath();
        MapImporter importer = new MapImporter();

        Factory.getGameRepository().setRunning(true);

        try {
            importer.load(path);
        } catch (IOException e) {
//            Factory.getGameRepository().setRunning(false);
        }
    }

    private void spawn() {
        for (int i = 0; i <= Factory.getGameRepository().getGuardCount(); i++) {
            Factory.getPlayerRepository().spawn(Guard.class);
        }
    }

    private void gameLoop() throws Exception {
        String directoryPath = System.getProperty("user.dir") + "\\Recorder\\Game#" + GAME_ID + "\\Agents";

        Factory.getGameRepository().setRunning(true);
        Factory.getPlayerRepository().getStopWatch().start();
        int moveCount = 1;
        while (Factory.getGameRepository().isRunning()) {
            int agentId = 1;
            Long time = Factory.getPlayerRepository().getStopWatch().getDurationInSeconds();
            for (Agent agent : Factory.getPlayerRepository().getAgents()) {

                Long startTime = Factory.getPlayerRepository().getStopWatch().getDurationInSeconds();
                Angle move = agent.decide();
                Long endTime = Factory.getPlayerRepository().getStopWatch().getDurationInSeconds();
                Long moveTimeDecide = endTime - startTime;
                agent.execute(move);

                JSONArray agentJSON = new JSONArray(new File(directoryPath + "\\Agent#" + agentId));

                JSONObject moveJSON = new JSONObject();
                moveJSON.put("Move", moveCount);
                JSONObject agentCoordinates = new JSONObject();
                agentCoordinates.put("X", agent.getPlayer().getTile().getX());
                agentCoordinates.put("Y", agent.getPlayer().getTile().getY());
                moveJSON.put("Location", agentCoordinates);
                moveJSON.put("Time", time);
                moveJSON.put("Time to decide", moveTimeDecide);
                moveJSON.put("Exploration rate %", Factory.getPlayerRepository().calculateAgentExplorationRate(agent));
                moveJSON.put("Total Exploration rate %", Factory.getPlayerRepository().getExplorationPercentage());

                agentJSON.put(moveJSON);

                agentId++;
            }

            moveCount++;

            if (Factory.getPlayerRepository().getExplorationPercentage() >= 100) {
                System.out.println("Out of Iterations - Game Over");
                break;
            }
        }
    }
}
