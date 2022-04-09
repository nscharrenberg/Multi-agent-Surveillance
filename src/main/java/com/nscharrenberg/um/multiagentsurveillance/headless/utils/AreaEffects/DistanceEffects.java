package com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.ManhattanDistance;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.Effect.Audio;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.Effect.IAudioEffect;

import java.util.List;

public class DistanceEffects {

    public void areaEffects(Agent agent, List<Agent> agentList){

        agent.getPlayer().getAudioEffects().clear();

        Tile agentTile = agent.getPlayer().getTile();

        for(Agent someAgent : agentList){
            Tile someAgentTile = someAgent.getPlayer().getTile();
            IAudioEffect representedSoundOfAgent = someAgent.getPlayer().getRepresentedSound();

            int distance = (int) ManhattanDistance.compute(agentTile, someAgentTile);

            if (representedSoundOfAgent.isEffectReachable(distance)) {
                agent.getPlayer().getAudioEffects().add(new Audio(representedSoundOfAgent.computeEffectLevel(distance)));
            }

        }
    }
}
