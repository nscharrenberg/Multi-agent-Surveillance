package com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json;

import java.util.List;
import java.util.Map;

public class GameJSON {

    private int gameId;
    private int agentNum;
    private Map<Integer, List<String>> agentDescription;

    public GameJSON(int gameId, int agentNum, Map<Integer, List<String>> agentDescription) {
        this.gameId = gameId;
        this.agentNum = agentNum;
        this.agentDescription = agentDescription;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public void setAgentNum(int agentNum) {
        this.agentNum = agentNum;
    }

    public void setAgentDescription(Map<Integer, List<String>> agentDescription) {
        this.agentDescription = agentDescription;
    }

    public int gameId() {
        return gameId;
    }


    public int agentNum() {
        return agentNum;
    }


    public Map<Integer, List<String>> agentDescription() {
        return agentDescription;
    }
}
