package com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.CalculateDistance;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.EuclideanDistance;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect.Audio;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect.IAudioEffect;

import java.util.List;

public class DistanceEffects {

    private static final CalculateDistance calculateDistance = new EuclideanDistance();

    public static void areaEffects(Agent agent, List<Agent> agentList){

        agent.getPlayer().getAudioEffects().clear();

        Tile agentTile = agent.getPlayer().getTile();

        for(Agent someAgent : agentList){
            if(someAgent.equals(agent))
                continue;

            Tile someAgentTile = someAgent.getPlayer().getTile();
            IAudioEffect representedSoundOfAgent = someAgent.getPlayer().getRepresentedSound();

            int distance = (int) calculateDistance.compute(agentTile, someAgentTile);

            if (representedSoundOfAgent.isEffectReachable(distance)) {
                agent.getPlayer().getAudioEffects().add(representedSoundOfAgent.getAudioEffect(agent, someAgent, distance));
            }

        }
    }
}
