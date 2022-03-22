package com.nscharrenberg.um.multiagentsurveillance.gui.dataGUI;

import com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json.AgentJSON;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json.Coordinates;

import java.util.ArrayList;
import java.util.List;

public class DataHelper {

    protected final int[] agentToCompare = {0};

    /*
    ~~~~~Choose X and Y~~~~~~

    "Steps"
    "Time"
    "Time To Decide"
    "Coordinates" -> "X", "Y"
    "Total Exploration Rate"
    "Agent Exploration Rate"
     */
    protected final String[] X_and_Y = {"Time", "Total Exploration Rate"};


    public List<List<Coordinates>> createXYCoordinates(List<List<AgentJSON>> data){
        List<List<Coordinates>> xyCoordinates = new ArrayList<>();
        for (int i = 0; i < agentToCompare.length; i++) {
            List<AgentJSON> agentJsonData = data.get(agentToCompare[i]);

            xyCoordinates.add(new ArrayList<>());

            List<Coordinates> coordinates = xyCoordinates.get(i);

            for (AgentJSON agentJsonDatum : agentJsonData) {
                coordinates.add(grabData(agentJsonDatum));
            }
        }

        return xyCoordinates;
    }

    private Coordinates grabData(AgentJSON agent){
        float x = grabAgentData(X_and_Y[0], agent);
        float y = grabAgentData(X_and_Y[1], agent);
        return new Coordinates(x, y);
    }

    private float grabAgentData(String parameter, AgentJSON agent){
        switch (parameter){
            case "Steps" -> {
                return agent.getMoveNum();
            }
            case "Time" -> {
                return agent.getGameTime();
            }
            case "Time To Decide" -> {
                return agent.getTimeToDecide();
            }
            case "X" -> {
                Coordinates coordinates = agent.getCoordinates();
                return coordinates.x;
            }
            case "Y" -> {
                Coordinates coordinates = agent.getCoordinates();
                return coordinates.y;
            }
            case "Total Exploration Rate" -> {
                return agent.getTotalExplorationRate();
            }
            case "Agent Exploration Rate" -> {
                return agent.getAgentExplorationRate();
            }
            default -> throw new RuntimeException("Parameter is not found, DataHelper.java");
        }

    }
}
