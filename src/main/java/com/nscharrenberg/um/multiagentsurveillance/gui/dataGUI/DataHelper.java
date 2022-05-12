package com.nscharrenberg.um.multiagentsurveillance.gui.dataGUI;

import com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json.AgentJSON;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json.Coordinates;

import java.util.ArrayList;
import java.util.List;

public class DataHelper {

    protected final List<Integer> agentToCompare;
    protected final String[] X_and_Y;

    public DataHelper(List<Integer> agentToCompare, String[] x_and_Y) {
        this.agentToCompare = agentToCompare;
        this.X_and_Y = x_and_Y;
    }

    public List<List<Coordinates>> createXYCoordinates(List<List<AgentJSON>> data){
        List<List<Coordinates>> xyCoordinates = new ArrayList<>();
        for (int i = 0; i < agentToCompare.size(); i++) {
            List<AgentJSON> agentJsonData = data.get(agentToCompare.get(i));

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
                return agent.moveNum();
            }
            case "Time" -> {
                return agent.gameTime();
            }
            case "Time To Decide" -> {
                return agent.timeToDecide();
            }
            case "X" -> {
                Coordinates coordinates = agent.coordinates();
                return coordinates.x();
            }
            case "Y" -> {
                Coordinates coordinates = agent.coordinates();
                return coordinates.y();
            }
            case "Total Exploration Rate" -> {
                return agent.totalExplorationRate();
            }
            case "Agent Exploration Rate" -> {
                return agent.agentExplorationRate();
            }
            default -> throw new RuntimeException("Parameter is not found, DataHelper.java");
        }

    }
}
