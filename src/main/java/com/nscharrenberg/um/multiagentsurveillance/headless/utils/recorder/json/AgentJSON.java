package com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json;

public record AgentJSON(int moveNum, long gameTime, long timeToDecide,
                        Coordinates coordinates, float totalExplorationRate, float agentExplorationRate) {

    @Override
    public int moveNum() {
        return moveNum;
    }

    @Override
    public long gameTime() {
        return gameTime;
    }

    @Override
    public long timeToDecide() {
        return timeToDecide;
    }

    @Override
    public Coordinates coordinates() {
        return coordinates;
    }

    @Override
    public float totalExplorationRate() {
        return totalExplorationRate;
    }

    @Override
    public float agentExplorationRate() {
        return agentExplorationRate;
    }
}
