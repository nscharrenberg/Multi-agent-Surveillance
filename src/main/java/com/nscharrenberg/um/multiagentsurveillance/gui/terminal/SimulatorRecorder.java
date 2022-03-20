package com.nscharrenberg.um.multiagentsurveillance.gui.terminal;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Guard;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.TileArea;
import com.nscharrenberg.um.multiagentsurveillance.headless.repositories.PlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.StopWatch;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.files.MapImporter;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.GameConfigurationRecorder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.RecordHelper.GAME_ID;

public class SimulatorRecorder {

    private int RECORD = 4;

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

        List<JSONArray> JSONList = createJSONArrayAgent(Factory.getPlayerRepository().getAgents().size(), directoryPath);

        int moveCount = 1;
        int point = 0;
        while (Factory.getGameRepository().isRunning()) {
            int agentId = 0;
            Long time = Factory.getPlayerRepository().getStopWatch().getDurationInMillis();
            for (Agent agent : Factory.getPlayerRepository().getAgents()) {

                Long startTime = Factory.getPlayerRepository().getStopWatch().getDurationInMillis();
                Angle move = agent.decide();
                Long endTime = Factory.getPlayerRepository().getStopWatch().getDurationInMillis();
                Long moveTimeDecide = endTime - startTime;
                agent.execute(move);


                Long recordedStartTime = Factory.getPlayerRepository().getStopWatch().getDurationInMillis();
                JSONArray agentJSON = JSONList.get(agentId);

                JSONObject moveJSON = new JSONObject();
                moveJSON.put("Move", moveCount);
                moveJSON.put("X", agent.getPlayer().getTile().getX());
                moveJSON.put("Y", agent.getPlayer().getTile().getY());
                moveJSON.put("Time", time/1000.0);
                moveJSON.put("Time to decide", moveTimeDecide/1000.0);
                moveJSON.put("Exploration rate %", Factory.getPlayerRepository().calculateAgentExplorationRate(agent));
                moveJSON.put("Total Exploration rate %", Factory.getPlayerRepository().getExplorationPercentage());

                agentJSON.put(moveJSON);
                Long recordedEndTime = Factory.getPlayerRepository().getStopWatch().getDurationInMillis();

                Factory.getPlayerRepository().getStopWatch().minusMillis(recordedEndTime - recordedStartTime);

                agentId++;
            }

            moveCount++;

            if(point == RECORD){
                Long recordedStartTime = Factory.getPlayerRepository().getStopWatch().getDurationInMillis();
                System.out.println("Successfully stored recordings");
                writeData(JSONList, directoryPath);
                JSONList = createJSONArrayAgent(Factory.getPlayerRepository().getAgents().size(), directoryPath);
                Long recordedEndTime = Factory.getPlayerRepository().getStopWatch().getDurationInMillis();

                Factory.getPlayerRepository().getStopWatch().minusMillis(recordedEndTime - recordedStartTime);

                point = 0;
            } else {
                point++;
            }

            if (Factory.getPlayerRepository().getExplorationPercentage() >= 100) {
                System.out.println("Out of Iterations - Game Over");
                break;
            }
        }
    }

    private List<JSONArray> createJSONArrayAgent(int agentNum, String directoryPath) throws FileNotFoundException, JSONException {
        ArrayList<JSONArray> JSONArray = new ArrayList<>();
        for (int i = 0; i < agentNum; i++) {
            JSONArray.add(new JSONArray(new JSONTokener(new FileReader(directoryPath + "\\Agent#" + i))));
        }
        return JSONArray;
    }

    private void writeData(List<JSONArray> agentJSON, String directoryPath){
        for (int i = 0; i < agentJSON.size(); i++) {
            try (FileWriter file = new FileWriter(directoryPath + "\\Agent#" + i)) {
                file.write(agentJSON.get(i).toString());
            } catch (Exception e) {
                throw new RuntimeException("Error Agents recorder in GameConfigurationRecorder.java");
            }
        }
    }
}
