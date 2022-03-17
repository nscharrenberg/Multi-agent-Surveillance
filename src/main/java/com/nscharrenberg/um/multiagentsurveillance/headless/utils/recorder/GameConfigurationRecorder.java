package com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.Factory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import static com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.RecordHelper.GAME_ID;

public class GameConfigurationRecorder {


    public void setUpConfFiles() throws Exception {

        //Creating folders for recording
        String directoryPath = System.getProperty("user.dir") + "\\Recorder";
        File directory = new File(directoryPath);
        directory.mkdir();
        int fileCount = directory.list().length + 1;
        directoryPath += "\\Game#" + fileCount;
        directory = new File(directoryPath);
        directory.mkdir();

        //Set up Game ID
        GAME_ID = fileCount;

        //Creating Game Configuration file
        setUpGameConfiguration(fileCount, directoryPath);

        //Creating Agent files
        setUpFirstRecording(directoryPath);

    }

    private void setUpGameConfiguration(int fileCount, String directoryPath) throws JSONException {
        List<Agent> agentList = Factory.getPlayerRepository().getAgents();

        JSONArray jsonArray = new JSONArray();

        JSONObject gameConf =  new JSONObject();
        gameConf.put("GameId", fileCount);
        gameConf.put("Agent#", agentList.size());

        JSONObject agent = new JSONObject();
        for (int i = 0; i < agentList.size(); i++) {
            agent.put("" + (i+1), "Yamuchi,BFS"); //TODO: Grab this information from the object or GUI???
        }
        gameConf.put("Agents", agent);

        JSONObject game = new JSONObject();
        game.put("Game", gameConf);
        jsonArray.put(game);

        //Write the file in JSON format
        try (FileWriter file = new FileWriter(directoryPath + "\\Game_Configuration")){
            file.write(jsonArray.toString());
            System.out.println("Successfully created a Game Configuration file");
        } catch(Exception e){
            throw new RuntimeException("Error Game Configuration in GameConfigurationRecorder.java");
        }
    }

    private void setUpFirstRecording(String directoryPath) throws Exception {
        File agents = new File(directoryPath + "\\Agents");
        agents.mkdir();

        int agentId = 0;
        for (Agent agent : Factory.getPlayerRepository().getAgents()) {
            JSONArray agentJSON = new JSONArray();

            JSONObject moveJSON = new JSONObject();
            moveJSON.put("Move", 0);
            JSONObject agentCoordinates = new JSONObject();
            agentCoordinates.put("X", agent.getPlayer().getTile().getX());
            agentCoordinates.put("Y", agent.getPlayer().getTile().getY());
            moveJSON.put("Location", agentCoordinates);
            moveJSON.put("Time", 0);
            moveJSON.put("Time to decide", 0);
            moveJSON.put("Exploration rate %", Factory.getPlayerRepository().calculateAgentExplorationRate(agent));
            moveJSON.put("Total Exploration rate %", Factory.getPlayerRepository().getExplorationPercentage());

            agentJSON.put(moveJSON);

            try (FileWriter file = new FileWriter(agents.getPath() + "\\Agent#" + agentId)){
                file.write(agentJSON.toString());
            } catch(Exception e){
                throw new RuntimeException("Error Agents recorder in GameConfigurationRecorder.java");
            }

            agentId++;
        }
    }
}
