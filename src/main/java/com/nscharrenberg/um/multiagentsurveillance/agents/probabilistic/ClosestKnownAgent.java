package com.nscharrenberg.um.multiagentsurveillance.agents.probabilistic;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Player.Player;

public class ClosestKnownAgent {

    public static boolean isClosestKnownAgent(Tile closestKnownAgent, Player player, Tile tile){
         return closestKnownAgent == null
                || Math.abs(player.getTile().getX() - tile.getX()) < Math.abs(player.getTile().getX() - closestKnownAgent.getX())
                || Math.abs(player.getTile().getX() - tile.getX()) < Math.abs(player.getTile().getY() - closestKnownAgent.getY())
                || Math.abs(player.getTile().getY() - tile.getY()) < Math.abs(player.getTile().getX() - closestKnownAgent.getX())
                || Math.abs(player.getTile().getY() - tile.getY()) < Math.abs(player.getTile().getY() - closestKnownAgent.getY());
    }
}
