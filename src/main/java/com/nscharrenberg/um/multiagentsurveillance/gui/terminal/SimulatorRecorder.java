package com.nscharrenberg.um.multiagentsurveillance.gui.terminal;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
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
        Factory.getGameRepository().startGame();
        new GameConfigurationRecorder().setUpConfFiles();
        gameLoop();
    }

    private void gameLoop() throws Exception {
        String directoryPath = System.getProperty("user.dir") + "\\Recorder\\Game#" + GAME_ID + "\\Agents";

        Factory.getGameRepository().setRunning(true);
        Factory.getPlayerRepository().getStopWatch().start();

        List<JSONArray> JSONList = createJSONArrayAgent(Factory.getPlayerRepository().getAgents().size(), directoryPath);

        int moveCount = 1;
        int point = 0;

        IPlayerRepository playerRepository = Factory.getPlayerRepository();

        Angle[] agentAngles = new Angle[playerRepository.getAgents().size()];
        int agentNum = 0;
        for (Agent agent : playerRepository.getAgents()) {
            agentAngles[agentNum] = agent.getPlayer().getDirection();
            agentNum++;
        }


        while (Factory.getGameRepository().isRunning()) {
            int agentId = 0;
            Long time = playerRepository.getStopWatch().getDurationInMillis();
            for (Agent agent : playerRepository.getAgents()) {

                Long startTime = playerRepository.getStopWatch().getDurationInMillis();
                Angle move = agent.decide();

                Long endTime = playerRepository.getStopWatch().getDurationInMillis();
                Long moveTimeDecide = endTime - startTime;
                agent.execute(move);

                if(agentAngles[agentId].equals(move)) {
                    Long recordedStartTime = playerRepository.getStopWatch().getDurationInMillis();
                    JSONArray agentJSON = JSONList.get(agentId);

                    JSONObject moveJSON = new JSONObject();
                    moveJSON.put("Move", moveCount);
                    moveJSON.put("X", agent.getPlayer().getTile().getX());
                    moveJSON.put("Y", agent.getPlayer().getTile().getY());
                    moveJSON.put("Time", time / 1000.0);
                    moveJSON.put("Time to decide", moveTimeDecide / 1000.0);
                    moveJSON.put("Exploration rate %", playerRepository.calculateAgentExplorationRate(agent));
                    moveJSON.put("Total Exploration rate %", playerRepository.getExplorationPercentage());

                    agentJSON.put(moveJSON);
                    Long recordedEndTime = playerRepository.getStopWatch().getDurationInMillis();

                    Factory.getPlayerRepository().getStopWatch().minusMillis(recordedEndTime - recordedStartTime);
                }

                agentAngles[agentId] = move;

                agentId++;
            }

            moveCount++;

            if(point == RECORD){
                Long recordedStartTime = playerRepository.getStopWatch().getDurationInMillis();
                System.out.println("Successfully stored recordings");
                writeData(JSONList, directoryPath);
                JSONList = createJSONArrayAgent(playerRepository.getAgents().size(), directoryPath);
                Long recordedEndTime = playerRepository.getStopWatch().getDurationInMillis();

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
