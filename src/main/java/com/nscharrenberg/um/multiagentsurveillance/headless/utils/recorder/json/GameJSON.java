package com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json;

import java.util.Map;

public class GameJSON {

    private int gameId;
    private int agentNum;
    private Map<Integer, String> agentDescription;

    public GameJSON(int gameId, int agentNum, Map<Integer, String> agentDescription) {
        this.gameId = gameId;
        this.agentNum = agentNum;
        this.agentDescription = agentDescription;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getAgentNum() {
        return agentNum;
    }

    public void setAgentNum(int agentNum) {
        this.agentNum = agentNum;
    }

    public Map<Integer, String> getAgentDescription() {
        return agentDescription;
    }

    public void setAgentDescription(Map<Integer, String> agentDescription) {
        this.agentDescription = agentDescription;
    }
}
