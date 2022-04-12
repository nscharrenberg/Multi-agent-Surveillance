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

    public void setMoveNum(int moveNum) {
        this.moveNum = moveNum;
    }

    public void setGameTime(long gameTime) {
        this.gameTime = gameTime;
    }

    public void setTimeToDecide(long timeToDecide) {
        this.timeToDecide = timeToDecide;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setTotalExplorationRate(float totalExplorationRate) {
        this.totalExplorationRate = totalExplorationRate;
    }

    public void setAgentExplorationRate(float agentExplorationRate) {
        this.agentExplorationRate = agentExplorationRate;
    }

    public int moveNum() {
        return moveNum;
    }

    public long gameTime() {
        return gameTime;
    }

    public long timeToDecide() {
        return timeToDecide;
    }

    public Coordinates coordinates() {
        return coordinates;
    }

    public float totalExplorationRate() {
        return totalExplorationRate;
    }

    public float agentExplorationRate() {
        return agentExplorationRate;
    }
}
