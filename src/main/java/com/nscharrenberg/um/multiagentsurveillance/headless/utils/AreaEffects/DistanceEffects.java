package com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.Effect.Effect;

import java.util.Arrays;
import java.util.List;

public class DistanceEffects {

    private final List<Effect> effectList;

    public DistanceEffects(Effect... effects){
        this.effectList = Arrays.asList(effects);
    }


    public void areaEffects(Agent agent, List<Agent> agentList){

        Tile agentTile = agent.getPlayer().getTile();

        for(Agent someAgent : agentList){
            Tile someAgentTile = someAgent.getPlayer().getTile();
            int distance = computeDistance(agentTile, someAgentTile);
            for(Effect effect : effectList) {
                if (effect.isEffectReachable(distance)) {
                    someAgent.getEffects().add(effect.computeEffectLevel(distance));
                }
            }
        }
    }

    private int computeDistance(Tile tileX, Tile tileY){
        int x = Math.abs(tileX.getX() - tileY.getX());
        int y = Math.abs(tileX.getY() - tileY.getY());
        return x + y;
    }
}
