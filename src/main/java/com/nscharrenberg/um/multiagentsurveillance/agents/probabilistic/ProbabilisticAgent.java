package com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.YamauchiAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic.evader.EvaderAgent;
import com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic.pursuer.PursuerAgent;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class ProbabilisticAgent extends YamauchiAgent {

    public State currentState = State.NORMAL;
    public Tile closestKnownAgent;

    public ProbabilisticAgent(Player player) {
        super(player);
    }

    public void checkVision(ProbabilisticAgent agent) {
        if (player.getVision() == null) return;

        for (Map.Entry<Integer, HashMap<Integer, Tile>> rowEntry : player.getVision().getRegion().entrySet()) {
            for (Map.Entry<Integer, Tile> colEntry : rowEntry.getValue().entrySet()) {
                Tile tile = colEntry.getValue();

                if (tile.hasGuard() && agent instanceof EvaderAgent) {
                    changeState(State.FLEE, tile);
                } else if(tile.hasIntruder() && agent instanceof PursuerAgent){
                    changeState(State.HUNT, tile);
                }
            }
        }
    }

    private void changeState(State state, Tile tile){
        currentState = state;

        if (ClosestKnownAgent.isClosestKnownAgent(closestKnownAgent, player, tile)) {
            closestKnownAgent = tile;
        }
    }
}
