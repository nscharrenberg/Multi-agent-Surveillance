package com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.YamauchiAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.AStar.AStar;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.pathfinding.BFS.BFS;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;

import com.google.gson.Gson;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json.GameJSON;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.RecordHelper.GAME_ID;

public class GameConfigurationRecorder {


    public void setUpConfFiles() {

        //Creating folders for recording
        String directoryPath = System.getProperty("user.dir") + "\\DataRecorder";
        File directory = new File(directoryPath);
        directory.mkdir();
        int fileCount = directory.list().length + 1;
        directoryPath += "\\Game#" + fileCount;
        directory = new File(directoryPath);
        directory.mkdir();

        //Set up GameConf ID
        GAME_ID = fileCount;

        //Creating GameConf Configuration file
        setUpGameConfiguration(fileCount, directoryPath);

        //Creating Agent files
        setUpAgentsFiles(directoryPath);

    }

    private void setUpGameConfiguration(int fileCount, String directoryPath)  {
        List<Agent> agentList = Factory.getPlayerRepository().getAgents();

        Gson gson = new Gson();

        Map<Integer, List<String>> agents = new HashMap();
        for (int i = 0; i < agentList.size(); i++) {
            List<String> agentSetting = getAgentSettings(agentList.get(i));
            agents.put(i, agentSetting);
        }

        GameJSON gameConf = new GameJSON(fileCount, agentList.size(), agents);

        //Write the file in JSON format
        try (FileWriter writer = new FileWriter(directoryPath + "\\Game_Configuration.json")) {
            gson.toJson(gameConf, writer);
            System.out.println("Successfully created the Game Configuration json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUpAgentsFiles(String directoryPath) {
        File agents = new File(directoryPath + "\\Agents");
        agents.mkdir();
        for (int i = 0; i < Factory.getPlayerRepository().getAgents().size();i++) {

            try (FileWriter file = new FileWriter(agents.getPath() + "\\Agent#" + i + ".json")){
                file.write("");
            } catch(Exception e){
                throw new RuntimeException("Error Agents recorder in GameConfigurationRecorder.java");
            }
        }
    }

    private List<String> getAgentSettings(Agent agent){
        List<String> agentSettings = new ArrayList<>();
        if(agent instanceof YamauchiAgent){
            agentSettings.add("Yamuchi");
        }

        if(agent.getPathFindingAlgorithm() instanceof BFS){
            agentSettings.add("BFS");
        } else if(agent.getPathFindingAlgorithm() instanceof AStar){
            agentSettings.add("AStar");
        } else if(agent.getPathFindingAlgorithm() == null){
            agentSettings.add("null");
        }
        return agentSettings;
    }
}
