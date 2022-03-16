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


    public void setUpConfFile() throws JSONException {

        //Creating folders for recording
        String directoryPath = System.getProperty("user.dir") + "\\Recorder";
        File directory = new File(directoryPath);
        int fileCount = directory.list().length + 1;
        directoryPath += "\\Game#" + fileCount;
        directory = new File(directoryPath);
        directory.mkdir();

        //Set up Game ID
        GAME_ID = fileCount;

        //Creating Game Configuration file
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

        //Creating folder for Agents
        File agents = new File(directoryPath + "\\Agents");
        agents.mkdir();

        //Creation files for Agents
        for (int i = 0; i < agentList.size(); i++) {
            JSONArray jsonArrayAgent = new JSONArray();
            String agent_num = "Agent#"+ (i+1);
            jsonArrayAgent.put(agent_num);
            try (FileWriter file = new FileWriter(agents.getPath() + "\\" + agent_num)){
                file.write(jsonArrayAgent.toString());
            } catch(Exception e){
                throw new RuntimeException("Error Agents recorder in GameConfigurationRecorder.java");
            }
        }
    }
}
