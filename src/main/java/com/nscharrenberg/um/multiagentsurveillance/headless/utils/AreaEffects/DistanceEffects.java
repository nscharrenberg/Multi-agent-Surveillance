package com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects;

import com.nscharrenberg.um.multiagentsurveillance.agents.shared.Agent;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.CalculateDistance;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.EuclideanDistance;
import com.nscharrenberg.um.multiagentsurveillance.headless.contracts.repositories.IGameRepository;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.AreaEffects.AudioEffect.ISoundEffect;
import com.nscharrenberg.um.multiagentsurveillance.headless.utils.Vision.Geometrics;

import java.util.List;

public class DistanceEffects {

    private static final CalculateDistance calculateDistance = new EuclideanDistance();

    public static void areaEffects(Agent agent, List<Agent> agentList, boolean canHearThroughWalls){

        agent.getPlayer().getSoundEffects().clear();

        Tile agentTile = agent.getPlayer().getTile();

        for(Agent someAgent : agentList){
            if(someAgent.equals(agent))
                continue;

            Tile someAgentTile = someAgent.getPlayer().getTile();
            ISoundEffect representedSoundOfAgent = someAgent.getPlayer().getRepresentedSound();

            int distance = (int) calculateDistance.compute(agentTile, someAgentTile);

            if (representedSoundOfAgent.isEffectReachable(distance)) {
                if (!canHearThroughWalls) {
                    boolean flag = false;
                    Geometrics geo = new Geometrics();
                    for (Tile tile : geo.getIntersectingTiles(agentTile, someAgentTile)) {

                        if (tile.isWall()) {
                            flag = true;
                            System.out.println("Am a wall");
                            break;
                        }
                    }
                    if(flag) {
                        System.out.println("Am flaggin");
                        continue;
                    }
                }

                agent.getPlayer().getSoundEffects().add(representedSoundOfAgent.getSoundEffect(agent, someAgent, distance));
            }

        }
    }
}
