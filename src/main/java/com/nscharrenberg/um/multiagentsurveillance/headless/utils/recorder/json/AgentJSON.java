package com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json;


import java.util.List;

//JSONObject moveJSON = new JSONObject();
//                    moveJSON.put("Move", moveCount);
//                    moveJSON.put("X", agent.getPlayer().getTile().getX());
//                    moveJSON.put("Y", agent.getPlayer().getTile().getY());
//                    moveJSON.put("Time", time / 1000.0);
//                    moveJSON.put("Time to decide", moveTimeDecide / 1000.0);
//                    moveJSON.put("Exploration rate %", playerRepository.calculateAgentExplorationRate(agent));
//                    moveJSON.put("Total Exploration rate %", playerRepository.getExplorationPercentage());
//
//                    agentJSON.put(moveJSON);
//                    Long recordedEndTime = playerRepository.getStopWatch().getDurationInMillis();
//
//                    Factory.getPlayerRepository().getStopWatch().minusMillis(recordedEndTime - recordedStartTime);
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
