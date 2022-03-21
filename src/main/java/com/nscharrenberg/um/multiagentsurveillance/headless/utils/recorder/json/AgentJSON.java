package com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json;

public class AgentJSON {

    private int moveNum;
    private long gameTime;
    private long timeToDecide;
    private Coordinates coordinates;
    private float totalExplorationRate;
    private float agentExplorationRate;

    public AgentJSON(int moveNum, long gameTime, long timeToDecide, Coordinates coordinates, float totalExplorationRate, float agentExplorationRate) {
        this.moveNum = moveNum;
        this.gameTime = gameTime;
        this.timeToDecide = timeToDecide;
        this.coordinates = coordinates;
        this.totalExplorationRate = totalExplorationRate;
        this.agentExplorationRate = agentExplorationRate;
    }

    public int getMoveNum() {
        return moveNum;
    }

    public void setMoveNum(int moveNum) {
        this.moveNum = moveNum;
    }

    public long getGameTime() {
        return gameTime;
    }

    public void setGameTime(long gameTime) {
        this.gameTime = gameTime;
    }

    public long getTimeToDecide() {
        return timeToDecide;
    }

    public void setTimeToDecide(long timeToDecide) {
        this.timeToDecide = timeToDecide;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public float getTotalExplorationRate() {
        return totalExplorationRate;
    }

    public void setTotalExplorationRate(float totalExplorationRate) {
        this.totalExplorationRate = totalExplorationRate;
    }

    public float getAgentExplorationRate() {
        return agentExplorationRate;
    }

    public void setAgentExplorationRate(float agentExplorationRate) {
        this.agentExplorationRate = agentExplorationRate;
    }
}
