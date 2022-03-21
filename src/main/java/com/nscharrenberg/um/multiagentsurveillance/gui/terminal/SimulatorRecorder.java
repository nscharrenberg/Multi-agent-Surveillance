package com.nscharrenberg.um.multiagentsurveillance.gui.terminal;

import com.google.gson.Gson;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IPlayerRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.GameConfigurationRecorder;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json.AgentJSON;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json.Coordinates;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
        IPlayerRepository playerRepository = Factory.getPlayerRepository();
        List<Agent> agents = playerRepository.getAgents();

        Factory.getGameRepository().setRunning(true);
        playerRepository.getStopWatch().start();

        List<List<AgentJSON>> data = createJsonAgentList(agents, playerRepository);

        int moveCount = 1;

        while (Factory.getGameRepository().isRunning()) {
            int agentId = 0;
            long time = (long) (playerRepository.getStopWatch().getDurationInMillis()/1000.0);
            for (Agent agent : agents) {

                long startTime = playerRepository.getStopWatch().getDurationInMillis();
                Angle move = agent.decide();

                long endTime = playerRepository.getStopWatch().getDurationInMillis();
                long moveTimeDecide = (long) ((endTime - startTime)/1000.0);
                agent.execute(move);

                List<AgentJSON> listAgentJSON = data.get(agentId);
                listAgentJSON.add(new AgentJSON(moveCount, time, moveTimeDecide,
                        new Coordinates(agent.getPlayer().getTile().getX(), agent.getPlayer().getTile().getY()),
                        playerRepository.getExplorationPercentage(), playerRepository.calculateAgentExplorationRate(agent)));

                agentId++;
            }

            moveCount++;
        }

        System.out.println("100% Achieved");

        writeJsonData(data);
    }

    private List<List<AgentJSON>> createJsonAgentList(List<Agent> agents, IPlayerRepository playerRepository) {
        List<List<AgentJSON>> data = new ArrayList<>();

        for (int i = 0; i < agents.size(); i++) {
            data.add(new ArrayList<>());
            data.get(i).add(new AgentJSON(0, 0, 0,
                    new Coordinates(agents.get(i).getPlayer().getTile().getX(), agents.get(i).getPlayer().getTile().getY()),
                    playerRepository.getExplorationPercentage(), playerRepository.calculateAgentExplorationRate(agents.get(i))));
        }

        return data;
    }

    private void writeJsonData(List<List<AgentJSON>> data){
        String directoryPath = System.getProperty("user.dir") + "\\DataRecorder\\Game#" + GAME_ID + "\\Agents";
        File agents = new File(directoryPath);
        agents.mkdir();

        Gson gson = new Gson();
        //Write the file in JSON format
        for (int i = 0; i < data.size(); i++) {
            try (FileWriter writer = new FileWriter(directoryPath + "\\Agent#" + i + ".json")) {
                gson.toJson(data.get(i), writer);
                System.out.println("Successfully created the Agent#" + i + ".json");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
