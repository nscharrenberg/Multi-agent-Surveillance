package com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json;

import java.util.List;
import java.util.Map;

public record GameJSON(int gameId, int agentNum, Map<Integer, List<String>> agentDescription) {

    @Override
    public int gameId() {
        return gameId;
    }

    @Override
    public int agentNum() {
        return agentNum;
    }

    @Override
    public Map<Integer, List<String>> agentDescription() {
        return agentDescription;
    }
}
